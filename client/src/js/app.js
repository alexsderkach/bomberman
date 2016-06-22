var wsUrl = 'ws://' + window.location.host;
var socket = new WebSocket(`${wsUrl}`);
var sessionId;
var site;
var players = {};
var bombs = [];
var userId;
var bombRadius;
var bombUrl = `http://${window.location.host}/bomb.png`;
var bombImg = new Image();
bombImg.src = bombUrl;

socket.onclose = () => {
    alert("Your connection was closed due to inactivity.");
    console.log("You were disconnected from the server");
};

socket.onerror = function (event) {
    console.log(event);
};

socket.onmessage = event => {
    var receivedEvent = JSON.parse(event.data);
    var data = receivedEvent.data;
    console.log(receivedEvent);
    switch (receivedEvent.type) {
        case 'CLIENT_INIT':
            sessionId = data.sessionId;
            break;
        case 'GAME_CREATION':
            var state = data.state;
            bombRadius = state.bombRadius;
            site = state.site;
            userId = state.players[state.userPlayerIndex].id;
            initPlayers(state.playerPositionMap);
            drawMap();
            break;
        case 'PLAYER_POSITION_MODIFICATION':
            players[data.player.id].x = data.position.x;
            players[data.player.id].y = data.position.y;
            drawMap();
            break;
        case 'BOMB_ALLOCATION':
            bombs.push(data.position);
            drawMap();
            break;
        case 'BOMB_ACTIVATION':
            var index = bombs.findIndex(p => data.position.x == p.x && data.position.y == p.y);
            bombs.splice(index, 1);
            console.log(data.position, index, bombs);
            drawMap();
            break;
        case 'PLAYER_DEATH':
            delete players[data.player.id];
            if (userId == data.player.id) {
                alert('Game Over');
                location.reload();
            } else if (Object.keys(players).length == 1) {
                alert("You won");
                location.reload();
            }
            drawMap();
            break;
        case 'CELL_DISAPPEARANCE':
            site[data.position.x][data.position.y] = 'EMPTY';
            drawMap();
            break;
        case 'MAX_SIMULTANEOUS_BOMBS_INCREMENT':
            console.log(data);
            document.getElementById('bombInfo').innerHTML = `You can place ${data.maxSimultaneousBombs} bombs simultaneously`;
            break;
    }
};

function initPlayers(playersPositions) {
    let colors = ['purple', '#5F9EA0', 'blue', 'green'];
    var i = 0;
    for (var player in playersPositions) {
        let position = playersPositions[player];
        var id = /\(id=(.+)\)/g.exec(player)[1];
        players[id] = {
            id: id,
            x: position.x,
            y: position.y,
            color: colors[i++]
        };
    }
}

function drawMap() {
    let cellSize = 60;
    let canvas = document.getElementById("canvas");
    let ctx = canvas.getContext("2d");

    ctx.globalAlpha = 0.7;

    canvas.width = site.length * cellSize;
    canvas.height = site[0].length * cellSize;

    for (var i = 0; i < site.length; i++) {
        for (var j = 0; j < site[i].length; j++) {
            drawCell(ctx, site[i][j], i * cellSize, j * cellSize, cellSize);
        }
    }

    var affectedPositions = [];
    for (var bomb of bombs) {
        var affected = getAffectedPositions(bomb);
        affectedPositions = affectedPositions.concat(affected);
    }
    // affectedPositions = new Set(affectedPositions);
    var visited = [];
    for (var position of affectedPositions) {
        var index = visited.findIndex(p => position.x == p.x && position.y == p.y);
        if (index < 0) {
            visited.push(position);
            drawBombAreaCell(ctx, position.x, position.y, cellSize);
        }
    }
    for (var bomb of bombs) {
        drawBomb(ctx, bomb, cellSize);
    }

    for (var playerId in players) {
        drawPlayer(ctx, players[playerId], cellSize);
    }
}

function drawCell(ctx, cell, x, y, size) {
    let colors = {
        'EMPTY': 'white',
        'VANISHING_BLOCK': '#808080',
        'PERMANENT_BLOCK': 'black'
    };
    ctx.fillStyle = colors[cell];
    ctx.fillRect(x, y, size, size);
    ctx.fill();
}

function drawPlayer(ctx, player, size) {
    ctx.fillStyle = player.color;
    ctx.beginPath();
    ctx.arc(player.x * size + size / 2, player.y * size + size / 2.5, (size / 2) * 0.5, 0, 2 * Math.PI, false);
    ctx.fill();

    let fontSize = 12;
    ctx.font = `${fontSize}px Arial`;
    let textMetrics = ctx.measureText(player.id);
    ctx.fillStyle = player.color;
    ctx.fillText(player.id, player.x * size + (size - textMetrics.width) / 2, player.y * size + size - fontSize * 0.2);
    ctx.fill();
}

function drawBomb(ctx, bomb, size) {
    ctx.drawImage(bombImg, bomb.x * size + size * 0.2, bomb.y * size + size * 0.2, size * 0.6, size * 0.6);
}

function getAffectedPositions(bomb) {
    var affected = [];
    affected = affected.concat(getAffected(bomb, bomb.x, bomb.y));
    for (var i = 1; i < bombRadius; i++) {
        affected = affected.concat(getAffected(bomb, bomb.x - i, bomb.y));
        affected = affected.concat(getAffected(bomb, bomb.x + i, bomb.y));
        affected = affected.concat(getAffected(bomb, bomb.x, bomb.y - i));
        affected = affected.concat(getAffected(bomb, bomb.x, bomb.y + i));
    }
    return affected;
}

function getAffected(bomb, x, y) {
    let width = site.length;
    let height = site[0].length;
    if (x < 0 || y < 0 || x >= width || y >= height) {
        return [];
    }

    if ((bomb.x > 0 && site[bomb.x - 1][bomb.y] == 'PERMANENT_BLOCK' && x < bomb.x)
        || (bomb.x + 1 < width && site[bomb.x + 1][bomb.y] == 'PERMANENT_BLOCK' && x > bomb.x)
        || (bomb.y > 0 && site[bomb.x][bomb.y - 1] == 'PERMANENT_BLOCK' && y < bomb.y)
        || (bomb.y + 1 < height && site[bomb.x][bomb.y + 1] == 'PERMANENT_BLOCK' && y > bomb.y)) {
        return [];
    }
    return [{x: x, y: y}];
}

function drawBombAreaCell(ctx, x, y, size) {
    ctx.fillStyle = "rgba(255, 0, 0, 0.5)";
    ctx.fillRect(x * size, y * size, size, size);
    ctx.fill();
}

document.onkeydown = (event) => {
    switch (event.keyCode) {
        case 37:
            event.preventDefault();
            $.post('/event/move', JSON.stringify({'sessionId': sessionId, 'direction': 'left'}));
            break;
        case 38:
            event.preventDefault();
            $.post('/event/move', JSON.stringify({'sessionId': sessionId, 'direction': 'up'}));
            break;
        case 39:
            event.preventDefault();
            $.post('/event/move', JSON.stringify({'sessionId': sessionId, 'direction': 'right'}));
            break;
        case 40:
            event.preventDefault();
            $.post('/event/move', JSON.stringify({'sessionId': sessionId, 'direction': 'down'}));
            break;
        case 32:
            event.preventDefault();
            $.post('/event/allocate_bomb', JSON.stringify({'sessionId': sessionId}));
            break;
    }
};


var start = new Date().getTime(),
    time = 0,
    elapsed = '0.0';

window.setTimeout(instance, 100);

function instance() {
    time += 100;

    elapsed = Math.floor(time / 100) / 10;
    if (Math.round(elapsed) == elapsed) {
        elapsed += '.0';
    }

    document.getElementById("timer").innerHTML = elapsed;

    var diff = (new Date().getTime() - start) - time;
    window.setTimeout(instance, (100 - diff));
}