function stairMover() {
    if (standingStair1) {
        standingStair1.moveUp(0);
        standingStair1.moveDown(0.5);
        standingStair1.moveLeft(0);
        standingStair1.moveRight(0);
     }

    if (standingStair2) {
        standingStair2.moveUp(0);
        standingStair2.moveDown(0);
        standingStair2.moveLeft(0);
        standingStair2.moveRight(0);
    }

    if (standingStair3) {
        standingStair3.moveUp(0);
        standingStair3.moveDown(0);
        standingStair3.moveLeft(0);
        standingStair3.moveRight(0);
    }
}

function handlePlayer(key, set) {
    switch (key) {
      case "A".charCodeAt(0):
      case "a".charCodeAt(0):
        player.LEFT = set;
        player.JUMP = true;
        break;
      case "D".charCodeAt(0):
      case "d".charCodeAt(0):
        player.RIGHT = set;
        player.JUMP = true;
        break;
    }
}