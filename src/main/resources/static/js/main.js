'use strict';

const usernamePage = document.querySelector('#username-page');
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
let username =  null;//currentUser.username;
let firstName = null;//currentUser.firstName;
let selectedUserId = null;
let onlineUsers = new Set(); // Keep track of online users
let previousWindow=null;
var activeUsersInWindow = new Set();
let notifications = [];


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

async function onSameWindow(payload) {
    const data = JSON.parse(payload.body);
    const { sender, recipientId, event } = data;

    if (recipientId !== username) return;

    if (event === "JOINED") {
        activeUsersInWindow.add(sender);
        //        console.log(`${sender} joined your window. Current watchers:`, [...activeUsersInWindow]);
    } else if (event === "LEFT") {
        activeUsersInWindow.delete(sender);
        //        console.log(`${sender} left your window. Current watchers:`, [...activeUsersInWindow]);
    }
    console.log("Active users on my chat window", activeUsersInWindow);
    // 👇 Re-fetch messages if the sender is the selected user
    if (selectedUserId === sender) {
        //        console.log(`Re-fetching messages because ${sender} ${event}`);
        await fetchAndDisplayUserChat();
    }
}


async function connect(event) {
    event.preventDefault();

    username = document.querySelector('#username').value.trim();
    firstName = document.querySelector('#firstName').value.trim();

    if (username && firstName) {
       usernamePage.classList.add('hidden');
       const notifData = await getNotificationSummary(username);

       const dropdown = document.getElementById("notificationDropdown");
       const notificationCountEl = document.getElementById("notification-count");

       if (notifData && notifData.length > 0) {
           // Clear previous notifications
           dropdown.innerHTML = "";

           for (let i = 0; i < notifData.length; i++) {
               // Create a new notifDiv for each notification
               const notifDiv = document.createElement("div");
               notifDiv.className = "notification";

               if (notifData && notifData[i] && notifData[i].chatId) {
                   // Improved formattedName: remove recipientId and the preceding underscore
                   var formattedName = notifData[i].chatId.replace(`${notifData[i].recipientId}`, "");
                   var newformattedName = formattedName.replace("_","");
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
    stompClient.subscribe(`/user/${username}/queue/typing`, onTypingStatus);
    stompClient.subscribe(`/user/${username}/queue/sameWindow`, onSameWindow);
    stompClient.subscribe(`/user/${username}/queue/leaveWindow`, onSameWindow);
    stompClient.subscribe(`/user/${username}/queue/Notifications`, onNotificationReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);
    stompClient.subscribe(`/topic/onlineUsers`, onActiveUsers);

    // Send the current user information to the server
    stompClient.send("/app/onlineUser", {}, JSON.stringify({ username: username, firstName: firstName }));

    // Register the connected user
    stompClient.send("/app/user/addUser", {}, JSON.stringify({ username: username, firstName: firstName, status: 'ONLINE' }));

    // Set the connected user's first name
    document.querySelector('#connected-user-firstName').textContent = firstName;
    findAndDisplayConnectedUsers().then();
}

async function onNotificationReceived(payload) {
    const notification = JSON.parse(payload.body);
    console.log("Notification received: ", notification);

    // Avoid duplicate entries based on chatId
    const exists = notifications.find(n => n.chatId === notification.chatId);
    if (!exists) {
        notifications.push(notification);
        updateNotificationUI();
    }
}

function updateNotificationUI() {
    const dropdown = document.getElementById('notificationDropdown');
    const countSpan = document.getElementById('notification-count');

    // Clear the dropdown
    dropdown.innerHTML = '';

    // Render current notifications
    notifications.forEach(notification => {
        const entry = document.createElement('div');
        entry.classList.add('notification');
        entry.setAttribute('data-chatid', notification.chatId);
        entry.textContent = `New message from ${notification.chatId}`;
        dropdown.appendChild(entry);
    });

    // Update the bell count
    if (notifications.length > 0) {
        countSpan.textContent = notifications.length;
        countSpan.style.display = 'inline';
    } else {
        countSpan.textContent = '0';
        countSpan.style.display = 'none';
    }
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
    onlineUsers = new Set(JSON.parse(payload.body));
    await findAndDisplayConnectedUsers(); // Ensure users are in the DOM first
    updateUserStatus();
}


async function findAndDisplayConnectedUsers() {

    const connectedUsersResponse = await fetch('/users');
    let connectedUsers = await connectedUsersResponse.json();
    connectedUsers = connectedUsers.filter(user => user.username !== username);
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
    // Remove 'active' class from all user items
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });

    // Show message form
    messageForm.classList.remove('hidden');
    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    const recipientId = clickedUser.getAttribute('id');
    selectedUserId = recipientId;

    // Notify previous window user that you left
    if (previousWindow && previousWindow !== recipientId) {
        var leaveMessage = {
            sender: username,
            recipientId: previousWindow,
            event: "LEFT"
        };
        stompClient.send("/app/leaveWindow", {}, JSON.stringify(leaveMessage));
    }

    // Notify the newly clicked user you joined their window
    var sameWindowMessage = {
        sender: username,
        recipientId: recipientId,
        event: "JOINED"
    };
    stompClient.send("/app/sameWindow", {}, JSON.stringify(sameWindowMessage));

    // Update previousWindow to the newly clicked user
    previousWindow = recipientId;

    // Fetch and display chat history
    fetchAndDisplayUserChat().then();

    // ✅ Remove notifications related to this recipient
    notifications = notifications.filter(n => n.chatId !== recipientId);

    // ✅ Re-render the notification dropdown and count
    updateNotificationUI();

    // Hide unread dot beside the user (if present)
    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    if (nbrMsg) {
        nbrMsg.classList.add('hidden');
    }
}




function displayMessage(senderId, content, status) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');

    if (senderId === username) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }

    const message = document.createElement('p');
    message.textContent = content;

    // Apply color based on message status
    if (status === "SENT") {
        message.style.backgroundColor = "orange";
    } else if (status === "DELIVERED") {
        message.style.backgroundColor = "green";
    } else {
        message.style.backgroundColor = "#f1f1f1"; // default fallback
    }

    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}


async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${username}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';

    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content, chat.status);
    });

    chatArea.scrollTop = chatArea.scrollHeight;
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    console.log("Active users while sending message ", activeUsersInWindow);

    if (messageContent && stompClient && selectedUserId && activeUsersInWindow) {
        const isRecipientActive = activeUsersInWindow.has(selectedUserId);
        console.log("what is the status: ", isRecipientActive);
        const chatMessage = {
            senderId: username,
            recipientId: selectedUserId,
            content: messageContent,
            timestamp: new Date().toISOString(),
            status: isRecipientActive ? "DELIVERED" : "SENT"
        };

        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(username, messageContent, chatMessage.status);
        messageInput.value = '';
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    event.preventDefault();
}



async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers();

    const message = JSON.parse(payload.body);
    console.log('Message received inside onMessageReceived', message);

    if (message.senderId && message.content && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content, message.status);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }
}


async function onTypingStatus(payload) {
    const message = JSON.parse(payload.body);
    // Only show typing status if the sender is the currently selected user
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




usernameForm.addEventListener('submit', connect,true);
messageForm.addEventListener('submit', sendMessage);
logout.addEventListener('click', onLogout, true);
//Add the typing div to the chat area.
chatArea.insertAdjacentHTML('afterend', '<div id="typing"></div>');

let typingIndicator;

//Add the oninput event to the message input.
messageInput.addEventListener('input', sendTyping);
window.onbeforeunload = () => onLogout();



