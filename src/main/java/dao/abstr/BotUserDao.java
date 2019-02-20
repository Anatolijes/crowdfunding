package dao.abstr;

import model.BotUser;

public interface BotUserDao {
    BotUser getBotUserById(int i);
    void addUser (BotUser botUser, Integer id);
}
