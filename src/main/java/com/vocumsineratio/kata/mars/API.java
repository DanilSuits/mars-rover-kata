/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

/**
 * @author Danil Suits (danil@vast.com)
 */
class API {
    interface Repository<T> {
        T get();

        void save(T theThing);
    }

    interface Squad {
        void run();
    }
}
