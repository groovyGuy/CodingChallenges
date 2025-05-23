package com.coding.challenge.dwarves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DwarfRafting {
    public static int solution(int raftSize, String barrelSeats, String takenSeats) {
        Square square = new Square(raftSize, barrelSeats, takenSeats);
        return square.calculateAdditions();
    }
}


class Square {
    public Quadrant topLeft;
    public Quadrant topRight;
    public Quadrant bottomLeft;
    public Quadrant bottomRight;

    public Square (int raftSize, String barrelSeats, String takenSeats) {
        topLeft = new Quadrant(raftSize, true, true);
        topRight = new Quadrant(raftSize, true, false);
        bottomLeft = new Quadrant(raftSize, false, true);
        bottomRight = new Quadrant(raftSize, false, false);
        topLeft.evaluateSeating(barrelSeats, takenSeats);
        topRight.evaluateSeating(barrelSeats, takenSeats);
        bottomLeft.evaluateSeating(barrelSeats, takenSeats);
        bottomRight.evaluateSeating(barrelSeats, takenSeats);
    }

    public int calculateAdditions() {
        int additions = balancedBoat() ? 0 : balanceSquare();
        if (!balancedBoat())
            return -1;
        additions += fillSquare();
        return additions;
    }

    private int fillSquare() {
        boolean pairOneNotBalanced = false;
        boolean pairTwoNotBalanced = false;
        int additions = 0;

        do {
            int result = addMore(topLeft, bottomRight);
            if (result == 0)
                pairOneNotBalanced = true;
            else
                additions += result;
            result = addMore(topRight, bottomLeft);
            if (result == 0)
                pairTwoNotBalanced = true;
            else
                additions += result;

        } while (!(pairOneNotBalanced && pairTwoNotBalanced));

        return additions;
    }

    private int balanceSquare() {
        int additions = balanceQuadrants(topLeft, bottomRight);
        additions += balanceQuadrants(topRight, bottomLeft);
        return additions;
    }

    private int balanceQuadrants(Quadrant q1, Quadrant q2) {
        int result = 0;
        while (q1.countOccupants() < q2.countOccupants() && q1.hasRoom()) {
            q1.add();
            result++;
        }
        while (q2.countOccupants() < q1.countOccupants() && q2.hasRoom()) {
            q2.add();
            result++;
        }
        return result;
    }

    private int addMore(Quadrant q1, Quadrant q2) {
        int result = 0;
        if (q1.hasRoom() && q2.hasRoom()) {
            q1.add();
            q2.add();
            if (balancedBoat())
                result = 2;
            else {
                q1.remove();
                q2.remove();
            }
        }
        return result;
    }

    private boolean balancedBoat() {
        return compareSides(topLeft, topRight, bottomLeft, bottomRight) == 0 &&
                compareSides(topLeft, bottomLeft, topRight, bottomRight) == 0;
    }

    /**
     1 = A is bigger
     0 = same
     -1 = A is smaller
     */
    public int compareSides(Quadrant a, Quadrant b, Quadrant c, Quadrant d) {
        int sizeA = a.countOccupants() + b.countOccupants();
        int sizeB = c.countOccupants() + d.countOccupants();
        int result = 0;
        if (sizeA > sizeB)
            result = 1;
        else if (sizeA < sizeB)
            result = -1;
        return result;
    }
}

class Quadrant {
    public final Map<Integer, Character> COLUMNS = initColumns();
    public int newOccupants = 0;
    public int existingOccupants = 0;
    public int openSeats = 0;
    public int barrels = 0;
    public int size;
    public int dimension;
    public Character leftColumn;
    public Character rightColumn;
    public int topRow;
    public int bottomRow;
    public boolean isTop;
    public boolean isLeft;

    public void add() {
        newOccupants++;
        openSeats--;
    }

    public void remove() {
        newOccupants--;
        openSeats++;
    }

    public Quadrant(int inSize, boolean isTopIn, boolean isLeftIn) {
        this.size = inSize;
        this.dimension = size / 2;
        this.isTop = isTopIn;
        this.isLeft = isLeftIn;
        setColumns();
        setRows();
    }

    public boolean hasRoom() {
        return openSeats > 0;
    }
    public int countOccupants() {
        return newOccupants + existingOccupants;
    }

    private void setColumns() {
        if (isLeft) {
            leftColumn = 'A';
            rightColumn = COLUMNS.get(dimension);
        } else {
            leftColumn = COLUMNS.get(dimension + 1);
            rightColumn = COLUMNS.get(size);
        }
    }

    private void setRows() {
        if (isTop) {
            topRow = 1;
            bottomRow = dimension;
        } else {
            topRow = dimension + 1;
            bottomRow = size;
        }
    }

    public void evaluateSeating(String barrelSeats, String occupiedSeats) {
        this.openSeats = dimension * dimension;
        calculateBarrels(barrelSeats);
        calculateOccupants(occupiedSeats);
    }

    private void calculateBarrels(String barrelSeats) {
        for (Seat seat : getSeats(barrelSeats)) {
            if (seatInQuadrant(seat)) {
                barrels++;
                openSeats--;
            }
        }
    }

    private void calculateOccupants(String occupiedSeats) {
        for (Seat seat : getSeats(occupiedSeats)) {
            if (seatInQuadrant(seat)) {
                existingOccupants++;
                openSeats--;
            }
        }

    }

    private List<Seat> getSeats(String values) {
        List<Seat> seats = new ArrayList<>();
        for (String s : values.split(" ")) {
            seats.add(new Seat(s));
        }
        return seats;
    }

    private boolean seatInQuadrant(Seat seat) {
        return (seat.row >= topRow && seat.row <= bottomRow &&
                seat.column >= leftColumn && seat.column <= rightColumn);
    }

    private Map<Integer, Character> initColumns() {
        Map<Integer, Character> map = new HashMap<>();
        char c = 'A';
        for (int i = 1; i <= 26; i++)
            map.put(i, c++);
        return map;
    }
}

class Seat {
    public Integer row;
    public Character column;

    public Seat(String seat) {
        row = Integer.parseInt("" + seat.charAt(0));
        column = seat.charAt(1);
    }
}