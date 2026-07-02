# DoorDash — Monsters University Edition (Java 8 + JavaFX 8)

## Visual Theme: Monsters University Board Game
- **Cream/beige tiles** for the entire board background
- **Colorful cartoon doors** for door cells (blue = SCARER, red = LAUGHER, gray-with-X = exhausted)
- **White cartoon socks with red stripes** for contamination cells
- **Green wooden ladders** for conveyor belts (snakes-and-ladders style)
- **"Monsters, Inc" yellow tags** for card cells and monster cells (with eyeball logo)
- **Wooden brown frame** around the whole grid
- **"MONSTERS UNIVERSITY" archway banner** at the top of the game scene
- **Cute round monster tokens** with one big eye and tiny horns

## Bug Fix: Players going out of the board
The overlay Pane that holds the player tokens was previously unsized,
which let JavaFX's StackPane centering drift the coordinate system away
from the grid's coordinate system. Fixed by:
1. Locking the overlay to the exact pixel dimensions of the grid
2. Clamping `placeToken()` so tokens can never leave the board area
3. Resetting `translateX/Y` to zero whenever a token's position is committed
4. Animation walks always start from the canonical position to prevent drift

## Run in Eclipse
1. **File → Import → Existing Projects** → select this folder → Finish
2. Confirm libraries: **JavaFX** user library + Java 8 JRE
3. Run config working directory: `${project_loc}`
4. Main class: `game.gui.Main`
5. Right-click `Main.java` → Run As → Java Application

## Team
Maryam · Mariam Mostafa · Hala Maged · Habeeba Essam  
Tutorial Group T-15 · German University in Cairo · CSEN 401
