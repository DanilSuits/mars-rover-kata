/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Application<Squad extends API.Squad> {
    private final API.Repository<Squad> repo;

    Application(API.Repository<Squad> repo) {
        this.repo = repo;
    }

    void handleCommand() {
        Squad squad = repo.get();

        squad.run();

        repo.save(squad);
    }
}
