package com.fernferret.wolfpound;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public enum AnimalAge {
    // Babies are -24000 since that's 20 ticks * 60 secs * 20 mins (one minecraft day)
    Baby(-24000),
    Adult(0);
    private int age;

    AnimalAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return this.age;
    }
}
