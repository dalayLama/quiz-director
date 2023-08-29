var gameId
var currentQuestionSubscription
var gameInfoInitialized = false

const url = 'http://localhost:8080'
const gamesEvents = "/user/games.events";
const currentQuestionEvent = "/user/currentQuestion";
const sendCurrentQuestionCommandDestination = "/topic/commands.sendCurrentQuestion"
const processAnswerDestination = "/topic/commands.processAnswer"


const connect = () => {
    const options = {'method': "GET", headers: {'Quiz-User-Id': document.getElementById("user-id").value}};
    fetch(url + "/api/v1/websocket/token", options)
        .then(response => response.json())
        .then(token => {
            stompClient = Stomp.over(new SockJS(url + "/sockjs"))
            var headers = {
                "Token-Id": token
            }
            stompClient.connect(headers, (frame) => {
                document.getElementById("bt-connect").disabled = true
                document.getElementById("bt-disconnect").disabled = false
                document.getElementById("bt-receive-question").disabled = false
                clearQuestions()
                clearGameResult()
                console.log('Connected: ' + frame)

                stompClient.subscribe(gamesEvents, function(gameEvent) {
                    const json = JSON.parse(gameEvent.body)
                    if (json.eventType === 'STARTED_GAME') {
                        setGame(json.gameInfo, true)
                        receiveCurrentQuestion()
                    } else {
                        setGame(null)
                        setGameResult(json.gameResult)
                    }
                });

                subscribeToCurrentQuestion()
            })
        })
}

const subscribeToCurrentQuestion = () => {
    currentQuestionSubscription = stompClient.subscribe(currentQuestionEvent, function(question) {
        var json = JSON.parse(question.body)
        if (!gameInfoInitialized) {
            setGame(json.gameInfo, false)
        }
        addQuestion(json)
    })
}

const unsubscribeToCurrentQuestion = () => {
    if (!currentQuestionSubscription) {
        return
    }
    currentQuestionSubscription.unsubscribe()
    currentQuestionSubscription = null
    disableQuestions()
}

const disconnect = () => {
    if (!!stompClient) {
        stompClient.disconnect(function (frame) {
            setGame(null)
            stompClient = null
            document.getElementById("bt-connect").disabled = false
            document.getElementById("bt-disconnect").disabled = true
            document.getElementById("bt-receive-question").disabled = true
            console.log("Disconnected")
        })    
    }
}

const receiveCurrentQuestion = () => {
    sendMsg(sendCurrentQuestionCommandDestination, null)
}

const sendMsg = (dest, msg) => stompClient.send(dest, {}, msg)

const addQuestion = (question) => {
    if (questionExists(question.gameInfo.id, question.number)) {
        return
    }

    disableQuestions()
    const label = document.createElement("label")
    label.innerHTML = question.question
    const select = document.createElement("select")
    question.answers.forEach(answer => {
        const option = document.createElement("option")
        option.text = answer
        option.id = answer
        select.appendChild(option)
    })
    const button = document.createElement("button")
    button.innerHTML = "answer"
    button.onclick = () => processAnswer(question.number, select.value)
    const div = document.createElement("div")
    div.setAttribute("id", generateQuestionId(question.gameInfo.id, question.number))
    div.classList.add("question")
    div.appendChild(label)
    const subDiv = document.createElement("div")
    subDiv.appendChild(select)
    subDiv.appendChild(button)
    div.appendChild(subDiv)

    document.getElementById("questions").appendChild(div)
}

const disableQuestions = () => {
    const questions = document.querySelectorAll("#questions .question")
    questions.forEach(questionElement => {
        const select = questionElement.getElementsByTagName("select")
        select.item(0).disabled = true
        const bt = questionElement.getElementsByTagName("button")
        if (bt.length > 0) {
            bt.item(0).remove()
        }
    })
}

const setGame = (gameInfo, initSubscriptionToQuestion) => {
    const gameInfoEl = document.getElementById("current-game-info")
    if (!gameInfo) {
        gameInfoEl.textContent = "";
        gameInfoInitialized = false
        unsubscribeToCurrentQuestion()
    } else if (gameInfo && !gameInfoInitialized) {
        gameId = gameInfo.id
        clearQuestions()
        const p = document.createElement("p")
        p.innerHTML = "Current game id - " + gameId
        gameInfoEl.appendChild(p)
        gameInfoInitialized = true
        if (initSubscriptionToQuestion) {
            subscribeToCurrentQuestion()
        }
        clearGameResult()
    }
}

const clearQuestions = () => {
    const questions = document.getElementById("questions")
    questions.textContent = "";
}

const questionExists = (gameId, questionNumber) => {
    return !!document.getElementById(generateQuestionId(gameId, questionNumber))
}

const processAnswer = (questionNumber, answer) => {
    console.log("answer for " + questionNumber + " is " + answer)
    const answerObj = {
        "gameId": gameId,
        "questionNumber": questionNumber,
        "answers": [answer]
    }
    sendMsg(processAnswerDestination, JSON.stringify(answerObj))
}

const generateQuestionId = (gameId, questionNumber) => {
    return "game-" + gameId + "-qNumber-" + questionNumber
}

const setGameResult = (gameResult) => {
    clearGameResult()
    const result = document.getElementById("gameResult")
    result.innerHTML = "result: totalQuestions - " + gameResult.totalQuestions + ", correct answers - " + gameResult.correctAnswers
}

const clearGameResult = () => {
    const result = document.getElementById("gameResult")
    result.innerHTML = ''
}