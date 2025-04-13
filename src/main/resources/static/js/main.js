'use strict';

//const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');


const profiles = [
  {
    id: "003dc47b-d8d0-4207-a1a1-f12070a09e7b",
    username: "kristykelly@example.com",
    firstName: "Kristy",
    lastName: "Kelly",
    location: "Toronto",
    profileBio: "A passionate photographer who loves capturing the beauty of nature.",
    profilePicture: null,
    profileMediaUrl: null,
    profileMediaList: [],
  },
  {
    id: "0d435a91-f344-495e-b805-2ff907ed558a",
    username: "test@user.com",
    firstName: "test",
    lastName: "user",
    location: "Brampton",
    profileBio: "",
    profilePicture: null,
    profileMediaUrl: null,
    profileMediaList: [],
  },
  {
    id: "0eb2afe9-d748-4901-9d8a-baa475522a73",
    username: "kathyball@example.com",
    firstName: "Kathy",
    lastName: "Ball",
    location: "Toronto",
    profileBio: "Their camera is always ready to preserve off-the-beaten-path destinations 📸🚀",
    profilePicture: null,
    profileMediaUrl: "http://216.152.71.22:1234/get-content/content/content-id/kathyball@example.com/f29aa717-138e-494a-94fe-5bb19ee35ed5",
    profileMediaList: [
      {
        media_url: "http://216.152.71.22:1234/get-content/content/content-id/kathyball@example.com/0d3f5e4f-6b64-4dfd-b3b6-08616060e2d7",
        media_type: "img"
      },
      {
        media_url: "http://216.152.71.22:1234/get-content/content/content-id/kathyball@example.com/5bcaca99-e4c4-4d41-943c-c5385fca0e7e",
        media_type: "img"
      },
      {
        media_url: "http://216.152.71.22:1234/get-content/content/content-id/kathyball@example.com/b6ea86f5-9f6b-4bbc-864d-1b9a3d3edb46",
        media_type: "img"
      },
      {
        media_url: "http://216.152.71.22:1234/get-content/content/content-id/kathyball@example.com/5c4202ad-f7d7-4a7a-9626-2e818b72ee7d",
        media_type: "img"
      },
      {
        media_url: "http://216.152.71.22:1234/get-content/content/content-id/kathyball@example.com/b15d499c-5bc9-4e52-81f9-ab17f34e1ba5",
        media_type: "img"
      },
      {
        media_url: "http://216.152.71.22:1234/get-content/content/content-id/kathyball@example.com/909557a1-62cf-4cdf-a2d8-c5ff188811db",
        media_type: "img"
      }
    ]
  },
  {
    id: "19718046-6156-4b18-8d91-88826252b672",
    username: "An@gmail.com",
    firstName: "Test",
    lastName: "Tets",
    location: "Brampton",
    profileBio: "",
    profilePicture: null,
    profileMediaUrl: null,
    profileMediaList: []
  }
];

let currentUser= profiles[3];

let stompClient = null;
let username = currentUser.username;
let firstName = currentUser.firstName;
let selectedUserId = null;
let onlineUsers = new Set(); // Keep track of online users


async function getNotificationSummary(username) {
    const userNotifResponse = await fetch(`/messages/${username}/allNotifications`);
    const userNotifications = await userNotifResponse.json();
    return userNotifications;
}

document.addEventListener("DOMContentLoaded", () => {
    const notificationBtn = document.getElementById("notificationBtn");
    const dropdown = document.getElementById("notificationDropdown");

    notificationBtn.addEventListener("click", () => {
        const notifications = dropdown.querySelectorAll('.notification');
        if (notifications.length > 0) {
            dropdown.classList.toggle("show");
            dropdown.classList.toggle("hidden");
        }
    });
});

async function connect(event) {
    event.preventDefault();

    if (username && firstName) {
       const notifData = await getNotificationSummary(username);
       console.log("Notifications for this user", notifData);

       const dropdown = document.getElementById("notificationDropdown");
       const notificationCountEl = document.getElementById("notification-count");

       if (notifData && notifData.length > 0) {
           // Clear previous notifications
           dropdown.innerHTML = "";

           for (let i = 0; i < notifData.length; i++) {
               // Create a new notifDiv for each notification
               const notifDiv = document.createElement("div");
               notifDiv.className = "notification";



               // Assuming notifData has an array or a way to get individual notification details
               // You'll need to modify this part based on the structure of your notifData
               // Example: if notifData.notifications is an array of messages
               if (notifData && notifData[i] && notifData[i].chatId) {
                   // Improved formattedName: remove recipientId and the preceding underscore
                   var formattedName = notifData[i].chatId.replace(`${notifData[i].recipientId}`, "");
                   var newformattedName = formattedName.replace("_","");

                   console.log("You have a new notification from ",  newformattedName);
                   notifDiv.textContent = `You have a new messages from ${newformattedName}`;
               } else {
                   // Handle cases where notification details are missing
                   notifDiv.textContent = `You have a new message`;
               }

               dropdown.appendChild(notifDiv);
           }

            // Update bell count
            notificationCountEl.textContent = notifData.length;
            notificationCountEl.style.display = 'inline-block'; // Show badge
        } else {
            notificationCountEl.textContent = "0";
            notificationCountEl.style.display = 'none'; // Hide if no notifications
        }

        usernameForm.classList.add('hidden');
        chatPage.classList.remove('hidden');
        document.getElementById('connected-user-fullname').textContent = firstName;

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
}

function onConnected() {
    stompClient.subscribe(`/user/${username}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/${username}/queue/typing`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);
    stompClient.subscribe(`/topic/onlineUsers`, onActiveUsers);

    stompClient.send("/app/onlineUser",
                {},
                JSON.stringify({username: username, firstName: firstName})
            );


    console.log("Sent message to '/app/typingStatus' ");

     //register the connected user
    stompClient.send("/app/user/addUser",
        {},
        JSON.stringify({username: username, firstName: firstName, status: 'ONLINE'})
    );

    document.querySelector('#connected-user-firstName').textContent = firstName;
    findAndDisplayConnectedUsers().then();
}

function sendTyping() {
    if (stompClient && selectedUserId) {
        const typingMessage = {
            sender: username,
            recipientId: selectedUserId,
            typing: true
        };

        stompClient.send("/app/typingStatus", {}, JSON.stringify(typingMessage));

        clearTimeout(typingIndicator);
        typingIndicator = setTimeout(() => {
            const typingDiv = document.getElementById("typing");
            if (typingDiv) {
                typingDiv.textContent = "";
            }
        }, 1000);
    }
}

async function onActiveUsers(payload) {
    console.log("Connected to active users websocket");
    console.log("Payload from onActiveUsers:", payload.body);

    onlineUsers = new Set(JSON.parse(payload.body));

    await findAndDisplayConnectedUsers(); // Ensure users are in the DOM first
    updateUserStatus();
}


async function findAndDisplayConnectedUsers() {
    // Simulate API response using hardcoded profiles
    let connectedUsers = profiles.filter(user => user.username !== currentUser.username);

    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });

    updateUserStatus();
}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.username;

    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.firstName;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.firstName;

    const statusIndicator = document.createElement('span');
    statusIndicator.classList.add('status-indicator'); // Add the status dot

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(statusIndicator);

    listItem.addEventListener('click', userItemClick);

    connectedUsersList.appendChild(listItem);
}


function updateUserStatus() {
    const userElements = document.querySelectorAll('.user-item');
    userElements.forEach(userElement => {
        const userId = userElement.id;
        let statusIndicator = userElement.querySelector('.status-indicator');

        if (!statusIndicator) {
            statusIndicator = document.createElement('span');
            statusIndicator.classList.add('status-indicator');
            userElement.appendChild(statusIndicator);
        }

        if (onlineUsers.has(userId)) {
            statusIndicator.classList.add('online');
            statusIndicator.classList.remove('offline');
        } else {
            statusIndicator.classList.add('offline');
            statusIndicator.classList.remove('online');
        }
    });
}


function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';

}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === username) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${username}/${selectedUserId}`);
    console.log("loading the path ..",`/messages/${username}/${selectedUserId}` );
    const userChat = await userChatResponse.json();
    console.log("loading user chat response ... ", userChat);
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}

function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    const messageContent = messageInput.value.trim();

    console.log("Selected user id ", selectedUserId, username, messageContent);

    if (messageContent && stompClient && selectedUserId) {
        const chatMessage = {
            senderId: username,
            recipientId: selectedUserId,
            content: messageContent,
            timestamp: new Date().toISOString()
        };

        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(username, messageContent);
        messageInput.value = '';
        chatArea.scrollTop = chatArea.scrollHeight;
    }
    event.preventDefault();
}


async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);

    // Handle typing notifications
    if (message.typing && message.sender !== username) {
        // Display "typing..." in the user list
        const userListItem = document.getElementById(message.sender);
        if (userListItem) {
            const typingIndicator = userListItem.querySelector('.typing-indicator');
            if (!typingIndicator) {
                const newTypingIndicator = document.createElement('span');
                newTypingIndicator.classList.add('typing-indicator');
                newTypingIndicator.textContent = " is typing...";
                userListItem.appendChild(newTypingIndicator);
            } else {
                typingIndicator.textContent = " is typing...";
            }

            clearTimeout(userListItem.typingTimeout);
            userListItem.typingTimeout = setTimeout(() => {
                const typingIndicator = userListItem.querySelector('.typing-indicator');
                if (typingIndicator) {
                    typingIndicator.remove();
                }
            }, 1000);
        }

        // Display "typing..." in the main chat area (if selected)
        if (selectedUserId === message.sender) {
            const typingDiv = document.getElementById("typing");
            if (typingDiv) {
                typingDiv.textContent = message.sender + " is typing...";
                clearTimeout(typingIndicator);
                typingIndicator = setTimeout(() => {
                    typingDiv.textContent = "";
                }, 1000);
            }
        }
    }

    // Handle chat messages (non-typing)
    if (!message.typing) {
        if (selectedUserId && selectedUserId === message.senderId) {
            // Display message in chat area
            displayMessage(message.senderId, message.content);
            chatArea.scrollTop = chatArea.scrollHeight;
        } else {
            // Display notification dot if not in the same chat
            const notifiedUser = document.querySelector(`#${message.senderId}`);
            if (notifiedUser && !notifiedUser.classList.contains('active')) {
                const nbrMsg = notifiedUser.querySelector('.nbr-msg');
                if (!nbrMsg) {
                    const newNbrMsg = document.createElement('span');
                    newNbrMsg.classList.add('nbr-msg');
                    newNbrMsg.textContent = '';
                    notifiedUser.appendChild(newNbrMsg);
                } else {
                    nbrMsg.classList.remove('hidden');
                    nbrMsg.textContent = '';
                }
            }
        }
    }

    // Update active user status
    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }
}


function onLogout() {
    stompClient.send("/app/user/disconnectUser",
        {},
        JSON.stringify({username: username, firstName: firstName, status: 'OFFLINE'})
    );

    stompClient.send("/app/offlineUser",
            {},
            JSON.stringify({username: username, firstName: firstName, status: 'OFFLINE'})
        );
    window.location.reload();
}




usernameForm.addEventListener('submit', connect)
messageForm.addEventListener('submit', sendMessage);
logout.addEventListener('click', onLogout, true);
//Add the typing div to the chat area.
chatArea.insertAdjacentHTML('afterend', '<div id="typing"></div>');

let typingIndicator;

//Add the oninput event to the message input.
messageInput.addEventListener('input', sendTyping);
window.onbeforeunload = () => onLogout();



