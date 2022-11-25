import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import {
  Button,
  Container,
  Divider,
  Item,
  Message,
  Segment,
} from "semantic-ui-react";
import { ADDR, getUkey } from "../../consts";

import mindImg from "../../images/mind.svg";

import { shuffle } from "../../utils";

import Offline from "../Offline";

const Main = ({ startQuiz }) => {
  const [category, setCategory] = useState("0");
  const [numOfQuestions, setNumOfQuestions] = useState(5);
  const [difficulty, setDifficulty] = useState("0");
  const [questionsType, setQuestionsType] = useState("0");
  const [countdownTime, setCountdownTime] = useState({
    hours: 0,
    minutes: 120,
    seconds: 0,
  });
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState(null);
  const [offline, setOffline] = useState(false);
  const [setupData, setSetupData] = useState(undefined);
  const [isFetching, setFetching] = useState(false);

  useEffect(() => {
    if (setupData || isFetching) return;

    setFetching(true);

    const fetchit = async () => {
      try {
        const res = await fetch(`${ADDR}/setup`).then((res) => res.json());
        setSetupData(res);
      } catch (err) {
        setError("Failed to load quiz, try refreshing");
        setSetupData(null);
        await new Promise((res) => setTimeout(res, 2000));
      } finally {
        setFetching(false);
      }
    };
    fetchit();
  }, [setupData, isFetching]);

  const handleTimeChange = (e, { name, value }) => {
    setCountdownTime({ ...countdownTime, [name]: value });
  };

  let allFieldsSelected = false;
  if (
    category &&
    numOfQuestions &&
    difficulty &&
    questionsType &&
    (countdownTime.hours || countdownTime.minutes || countdownTime.seconds)
  ) {
    allFieldsSelected = true;
  }

  const fetchData = () => {
    setProcessing(true);

    if (error) setError(null);
    const API = `${ADDR}/questions`;

    fetch(API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ukey: getUkey() }),
    })
      .then((respone) => respone.json())
      .then((data) =>
        setTimeout(() => {
          const { response_code, results } = data;

          if (response_code === 1) {
            const message = (
              <p>
                The API doesn't have enough questions for your query. (Ex.
                Asking for 50 Questions in a Category that only has 20.)
                <br />
                <br />
                Please change the <strong>No. of Questions</strong>,{" "}
                <strong>Difficulty Level</strong>, or{" "}
                <strong>Type of Questions</strong>.
              </p>
            );

            setProcessing(false);
            setError({ message });

            return;
          }

          results.forEach((element) => {
            element.options = shuffle(element.options);
          });

          setProcessing(false);
          startQuiz(
            results,
            // countdownTime.hours + countdownTime.minutes + countdownTime.seconds
            data.time
          );
        }, 1000)
      )
      .catch((error) =>
        setTimeout(() => {
          if (!navigator.onLine) {
            setOffline(true);
          } else {
            setProcessing(false);
            setError(error);
          }
        }, 1000)
      );
  };

  if ((setupData === undefined && error === null) || isFetching) {
    return <Offline />;
  }

  if (offline) return <Offline />;

  return (
    <Container>
      <Segment>
        <Item.Group divided>
          <Item>
            <Item.Image src={mindImg} />
            <Item.Content>
              <Item.Header>
                <h1>Quiz App</h1>
              </Item.Header>
              {error && (
                <Message error onDismiss={() => setError(null)}>
                  <Message.Header>Error!</Message.Header>
                  {error.message}
                </Message>
              )}
              <Divider />
              {setupData && (
                <>
                  <h3>Number of questions: {setupData.numberOfQuestions}</h3>
                  <h3>
                    Time: {Math.floor(setupData.time / 60)}min{" "}
                    {setupData.time % 60}s
                  </h3>
                </>
              )}
              <Item.Extra>
                <Button
                  primary
                  size="big"
                  icon="play"
                  labelPosition="left"
                  content={processing ? "Processing..." : "Start quiz"}
                  onClick={fetchData}
                  disabled={!allFieldsSelected || processing}
                />
              </Item.Extra>
            </Item.Content>
          </Item>
        </Item.Group>
      </Segment>
      <br />
    </Container>
  );
};

Main.propTypes = {
  startQuiz: PropTypes.func.isRequired,
};

export default Main;
