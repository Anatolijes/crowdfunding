import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;

public class Main {


	private static String PROXY_HOST = "170.84.204.128";
	private static Integer PROXY_PORT = 1080;


	public static void main(String[] args) {
		ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();

		DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
		defaultBotOptions.setProxyHost(PROXY_HOST);
		defaultBotOptions.setProxyPort(PROXY_PORT);
		defaultBotOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
		try{
			botsApi.registerBot(new UrbanSocializerBot(defaultBotOptions));
		}
		catch (TelegramApiRequestException e){
			e.printStackTrace();
		}



	}



}
