package dao.impl;

import dao.abstr.BotUserDao;
import model.BotUser;
import storage.Storage;

//а все вместе это такой способ проектирования приложений: Паттерн Синглтон.
public class BotUserDaoImpl implements BotUserDao {
        private BotUserDaoImpl() {}
        private static BotUserDaoImpl instance;
        public static BotUserDaoImpl getInstance(){
            if (instance == null) {
                instance = new BotUserDaoImpl();
            }
            return instance;
        }

    @Override
    public BotUser getBotUserById(int i) {
        return Storage.USERS_TABLE.get(i);
    }

    @Override
    public void addUser(BotUser botUser, Integer id) {
        Storage.USERS_TABLE.put(id,botUser);
    }

}
