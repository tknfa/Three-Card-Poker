package edu.uic.cs342.poker.model;

public final class Player {

    private final String name;
    private Hand hand;
    private int anteWager;
    private int pairPlusWager;
    private int playWager;
    private PlayerDecision decision = PlayerDecision.UNDECIDED;
    private int cumulativeWinnings;

    public Player(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name is required");
        }

        this.name = name;
    }

    public String getName() { return name; }
    public Hand getHand() { return hand; }
    public int getAnteWager() { return anteWager; }
    public int getPairPlusWager() { return pairPlusWager; }
    public int getPlayWager() { return playWager; }
    public PlayerDecision getDecision() { return decision; }
    public int getCumulativeWinnings() { return cumulativeWinnings; }

    public void setWagers(int ante, int pairPlus) {
        if (hand != null || decision != PlayerDecision.UNDECIDED) {
            throw new IllegalStateException("Wagers cannot change after dealing");
        }

        if (ante < 5 || ante > 25) {
            throw new IllegalArgumentException("Ante must be between $5 and $25");
        }

        if (pairPlus != 0 && (pairPlus < 5 || pairPlus > 25)) {
            throw new IllegalArgumentException("Pair Plus must be $0 or between $5 and $25");
        }

        anteWager = ante;
        pairPlusWager = pairPlus;
    }

    public void setHand(Hand hand) {
        if (hand == null) {
            throw new IllegalArgumentException("Player requires a hand");
        }

        if (this.hand != null || anteWager == 0) {
            throw new IllegalStateException("Confirm wagers before dealing a new hand");
        }

        this.hand = hand;
    }

    public void play() {
        checkDecision();
        playWager = anteWager;
        decision = PlayerDecision.PLAY;
    }

    public void fold() {
        checkDecision();
        playWager = 0;
        decision = PlayerDecision.FOLD;
    }

    public void checkDecision() {
        if (hand == null || decision != PlayerDecision.UNDECIDED) {
            throw new IllegalStateException("Player must have a hand and no prior decision");
        }
    }

    public void addWinnings(int amount) {
        cumulativeWinnings += amount;
    }

    public void resetRound() {
        hand = null;
        anteWager = 0;
        pairPlusWager = 0;
        playWager = 0;
        decision = PlayerDecision.UNDECIDED;
    }
 }
