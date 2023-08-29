var stompClient

const connect = () => {
    stompClient = Stomp.over(new SockJS('http://localhost:61613/ws'))
    stompClient.connect('user', 'password', (frame) => {
        console.log(frame)
    })
}

const disconnect = () => {
    if (!!stompClient) {
        stompClient.disconnect(function (frame) {
            console.log("Disconnected")
        })    
    }
}