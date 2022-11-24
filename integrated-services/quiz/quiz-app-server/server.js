const express = require("express");
const path = require("path");
const yaml = require("js-yaml");
const fs = require("fs");
var cors = require("cors");

const app = express();

const userResults = {};
const userTracking = {};

const params = JSON.parse(
  fs.readFileSync("params/params.json", {
    encoding: "utf-8",
  })
);
const userKeyToDescriptor = params.userMapping;
const timeS = params.timeS;

const shuffle = (array) => {
  array = [...array];

  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }

  return array;
};

const countCorrect = (results, quiz) => {
  return results
    .map(({ id, question, user_answer }) => {
      const q = quiz.find((q) => q.id === id);
      if (q && user_answer === `${q.correct}`) {
        return 1;
      }
      return 0;
    })
    .filter((r) => r === 1).length;
};

const convertToSupportedFormat = ({ test }) => {
  return test.map((q) => ({
    category: "",
    type: "multiple",
    difficulty: "",
    question: q.question,
    options: shuffle([...q.incorrect.map((x) => `${x}`), `${q.correct}`]),
    id: q.id,
  }));
};

app.use(express.json());
app.use(cors());

const rawQuiz = yaml.load(
  fs.readFileSync("questions/questions.yaml", { encoding: "utf-8" })
);
rawQuiz.test.forEach((el, idx) => (el["id"] = idx));

const quiz = convertToSupportedFormat(rawQuiz);

app.use(express.static(path.join(__dirname, "public", "build")));

app.get("/", function (req, res) {
  res.sendFile(path.join(__dirname, "public", "build", "index.html"));
});

app.get("/quiz-app", function (req, res) {
  res.sendFile(path.join(__dirname, "public", "build", "index.html"));
});

app.post("/submit", (req, res) => {
  const { userKey, results } = req.body;
  const descriptor = userKeyToDescriptor[userKey];

  if (!descriptor) {
    console.log(`No user with user key ${userKey}`);
    res.sendStatus(404);
    return;
  }

  const startTime = userTracking[req.body.ukey];
  if (Math.ceil((new Date().getTime() - startTime) / 1000) > timeS + 30) {
    res.sendStatus(400);
    return;
  }

  if (!userResults[descriptor]) {
    userResults[descriptor] = {
      score: countCorrect(results, rawQuiz.test),
      results,
    };

    fs.promises.writeFile("answers/answers.yaml", yaml.dump(userResults));
  }
  res.sendStatus(200);
});

app.post("/questions", (req, res) => {
  if (!req.body.ukey || !userKeyToDescriptor[req.body.ukey]) {
    res.sendStatus(400);
    return;
  }
  const startTime = userTracking[req.body.ukey];
  let remainingTime = timeS;

  if (startTime) {
    remainingTime =
      timeS - Math.ceil((new Date().getTime() - startTime) / 1000);
  } else {
    userTracking[req.body.ukey] = new Date().getTime();
  }

  res.send({
    time: remainingTime,
    results: quiz,
    response_code: 0,
  });
});

app.get("/setup", (req, res) => {
  res.send({
    time: timeS,
    numberOfQuestions: rawQuiz.test.length,
  });
});

app.listen(8080, () => {
  console.log("server running on port 8080");
});
