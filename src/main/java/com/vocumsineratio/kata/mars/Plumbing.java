/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Plumbing {
    interface Database<T> {
        T load();

        void store(T thing);
    }

    interface Parser<Document, Model> {
        Model parse(Document source);
    }

    interface Model<Document> {
        Document toDocument();
    }

    static class Repository<Document, M extends Model<Document>> implements API.Repository<M> {
        private final Database<Document> database;
        Parser<Document, M> parser;

        Repository(Database<Document> database, Parser<Document, M> parser) {
            this.database = database;
            this.parser = parser;
        }

        @Override
        public M get() {
            Document lines = database.load();
            return parser.parse(lines);
        }

        @Override
        public void save(M squad) {
            Document data = squad.toDocument();
            database.store(data);
        }
    }
}
