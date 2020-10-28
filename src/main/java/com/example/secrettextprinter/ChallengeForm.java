package com.example.secrettextprinter;


public class ChallengeForm {
    private String solution;

    public ChallengeForm(){

    }

    public ChallengeForm(int number, String solution) {
        this.solution = solution;
    }


    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
