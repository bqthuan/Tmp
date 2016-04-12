var randomString = require('randomstring');

function Server() {
    this.rooms = [];
    this.users = [];
};
function log(msg) {
    console.log(msg);

}
Server.prototype.getRoom = function (id) {
    for (var i = 0; i < this.rooms.length; ++i) {
        var room = this.rooms[i];
        if (room.id === id) {
            return room;
        }
    }
    return null;
};
Server.prototype.setup = function (io) {
    this.io = io;
    var self = this;
    io.on('connection', function (socket) {
        var user = JSON.parse(socket.handshake.query.user);
        user.id = socket.id;
        self.users.push(user);
        socket.emit('auth', {success: true});

        socket.on('createRoom', function (room) {
            room.id = randomString.generate(8);
            room.count = 0;
            self.rooms.push(room);
            io.sockets.emit('updateRoomList', self.rooms);
        });
        socket.on('updateRoomList', function () {
            socket.emit('updateRoomList', self.rooms);
        });
        socket.on('joinRoom', function (roomId) {
            socket.join(self.getRoom(roomId).name, function (error) {
                if (error) {

                } else {
                    self.getRoom(roomId).count += 1;
                    io.sockets.to(self.getRoom(roomId).name).emit('updateUserList', self.users);
                }
            });
        });
        socket.on('updateUserList', function () {
            socket.emit('updateUserList', self.users);
        });
        socket.on('leaveRoom', function (roomId) {
            self.getRoom(roomId).count -= 1;
            io.sockets.to(self.getRoom(roomId).name).emit('updateUserList', self.users);
        });
        socket.on('sendMessage', function (msg) {
            io.sockets.emit('sendMessage', {userId: socket.id, msg: msg});
        });
        socket.on('disconnect', function () {

        });
    });


};


var server = new Server();

module.exports = server;