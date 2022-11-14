import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import { Container, Menu } from "semantic-ui-react";
import { ADDR, getUkey } from "../../consts";

import Stats from "./Stats";

const Result = ({
  totalQuestions,
  correctAnswers,
  timeTaken,
  questionsAndAnswers,
  replayQuiz,
  resetQuiz,
}) => {
  const [activeTab, setActiveTab] = useState("Stats");
  const [done, setDone] = useState(false);
  const [trying, setTrying] = useState(false);

  useEffect(() => {
    if (done || trying) {
      return;
    }
    setTrying(true);

    const submiter = async () => {
      while (true) {
        try {
          const resp = await fetch(`${ADDR}/submit`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              userKey: getUkey(),
              results: questionsAndAnswers,
            }),
          });
          if (resp.ok) {
            setDone(true);
            break;
          }
        } catch (ignored) {}
        await new Promise((res) => setTimeout(res, 500));
      }
    };

    submiter();
  }, [questionsAndAnswers, trying, done]);

  const handleTabClick = (e, { name }) => {
    setActiveTab(name);
  };

  return (
    <Container>
      <Menu fluid widths={2}>
        <Menu.Item
          name="Stats"
          active={activeTab === "Stats"}
          onClick={handleTabClick}
        />
      </Menu>
      {activeTab === "Stats" && (
        <Stats
          totalQuestions={totalQuestions}
          correctAnswers={correctAnswers}
          timeTaken={timeTaken}
          replayQuiz={replayQuiz}
          resetQuiz={resetQuiz}
          done={done}
        />
      )}
      {/* {activeTab === 'QNA' && <QNA questionsAndAnswers={questionsAndAnswers} />} */}
      <br />
    </Container>
  );
};

Result.propTypes = {
  totalQuestions: PropTypes.number.isRequired,
  correctAnswers: PropTypes.number.isRequired,
  timeTaken: PropTypes.number.isRequired,
  questionsAndAnswers: PropTypes.array.isRequired,
  replayQuiz: PropTypes.func.isRequired,
  resetQuiz: PropTypes.func.isRequired,
};

export default Result;
