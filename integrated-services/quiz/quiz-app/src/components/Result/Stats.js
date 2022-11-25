import PropTypes from "prop-types";
import React from "react";
import { Header, Segment } from "semantic-ui-react";

import { calculateGrade, calculateScore, timeConverter } from "../../utils";

const Stats = ({
  totalQuestions,
  correctAnswers,
  timeTaken,
  replayQuiz,
  resetQuiz,
  done,
}) => {
  const score = calculateScore(totalQuestions, correctAnswers);
  const { grade, remarks } = calculateGrade(score);
  const { hours, minutes, seconds } = timeConverter(timeTaken);

  return (
    <Segment>
      <Header as="h3" textAlign="center" block>
        Total Questions: {totalQuestions}
      </Header>
      <Header as="h3" textAlign="center" block>
        Time Taken:{" "}
        {`${Number(hours)}h ${Number(minutes)}m ${Number(seconds)}s`}
      </Header>
      {done ? (
        <Header as="h3" textAlign="center" block>
          Your answers were saved successfully, you can close this window.
          Teacher didn't allow you to see the results.
        </Header>
      ) : (
        <Header as="h3" textAlign="center" block>
          Saving your answers, please don't close this window...
        </Header>
      )}
    </Segment>
  );
};

Stats.propTypes = {
  totalQuestions: PropTypes.number.isRequired,
  correctAnswers: PropTypes.number.isRequired,
  timeTaken: PropTypes.number.isRequired,
  replayQuiz: PropTypes.func.isRequired,
  resetQuiz: PropTypes.func.isRequired,
  done: PropTypes.bool.isRequired,
};

export default Stats;
