package keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

	public ReplyKeyboardMarkup createKeyboardRow(int keyboardRowQuantity, int keyboardButtonQuantity,
										  boolean setSelective, boolean setResizeKeyboard,boolean setOneTimeKeyboard, String... buttonsName){

		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(setSelective);
		replyKeyboardMarkup.setOneTimeKeyboard(setOneTimeKeyboard);
		replyKeyboardMarkup.setResizeKeyboard(setResizeKeyboard);
		KeyboardRow keyboardRow = new KeyboardRow();
		List<KeyboardRow> keyboard = new ArrayList<>();
        for(int i = 0; i<=keyboardRowQuantity; i++){
			for(int b = 0; b<=keyboardButtonQuantity; b++) {
				keyboard.add(keyboardRow);
			}
		}


		return replyKeyboardMarkup.setKeyboard(keyboard);
	}


}
