import model.BotUser;
import model.Project;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.xwpf.usermodel.*;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.abstr.BotUserService;
import service.abstr.ProjectService;
import service.impl.BotUserServiceImpl;
import service.impl.ProjectServiceImpl;
import storage.Storage;


import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UrbanSocializerBot extends TelegramLongPollingBot implements Serializable {
	private static final String BOT_NAME = "SolveTheProblemTogether";
	private static final String BOT_TOKEN = "726296784:AAG70XT-URXz69YvwPaKNQDivImfZAmFCOQ";
	private BotUserService botUserService = BotUserServiceImpl.getInstance();
	private ProjectService projectService = ProjectServiceImpl.getInstance();

	UrbanSocializerBot(DefaultBotOptions options) {
		super(options);
	}


	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}

	public void onUpdateReceived(Update update) {

		List<String> currentContext;
		Message message = update.getMessage();

		if (message != null) {
			String messageFromTelegram = message.getText();
			User userFromTelegram = message.getFrom();
			Integer currentUserId = userFromTelegram.getId();
			Long currentChatId = message.getChatId();
			int contextPosition = 0;

			if ("/start".equals(messageFromTelegram)) {
				if (!botUserService.isUserExistById(currentUserId)) {
					botUserService.addUser(new BotUser(), currentUserId);
				}


				BotUser currentUser = botUserService.getUser(currentUserId);


				ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

				replyKeyboardMarkup.setOneTimeKeyboard(true);
				KeyboardRow keyboardRow = new KeyboardRow();

				keyboardRow.add("Регистрация");
				keyboardRow.add("Текущие проекты");
				List<KeyboardRow> keyboard = new ArrayList<>();
				keyboard.add(keyboardRow);

				if (currentUser.isRegCompleated()) {
					KeyboardRow keyboardRow2 = new KeyboardRow();

					keyboardRow2.add("Добавить проект");
					keyboard.add(keyboardRow2);
				}

				replyKeyboardMarkup.setKeyboard(keyboard);
				sendMsg(currentChatId, "Добрый день.\n" +
						"Добрый пожаловать crowd-finding платформу.\n" +
						"Ее задачей является, сбор денег на реализацию,\n" +
						"проектов посвещенных благоустройству комуннальной среды.\n",replyKeyboardMarkup);

			} else {
				BotUser currentUser = botUserService.getUser(currentUserId);
				currentContext = currentUser.getContext();

				if (currentContext.isEmpty()) {
					switch (messageFromTelegram) {
						case "Регистрация": {
							sendMsg(currentChatId, "Регистрация.");
							sendMsg(currentChatId, "Пожалуйста, укажите ваш адрес.");
							currentContext.add("Регистрация");
							break;
						}
						case "Информация": {
							sendMsg(currentChatId, "Добро пожаловать.\n" +
									"Здесь вы можете предложить свой проект " +
									"по улучшению городской или районной инфраструктуры.");
							break;
						}
						case "Текущие проекты": {
							for (Map.Entry<Integer, Project> entry :
									Storage.PROJECTS_TABLE.entrySet()) {
								sendMsg(currentChatId, "Название: " + entry.getValue().getTitle() + "\n"
										+ "Описание: " + entry.getValue().getDescription() + "\n"
										+ "Сумма: " + entry.getValue().getAllSum() + "\n"
										+ "Собрано: " + entry.getValue().getCurrentSum() + "\n"
										+ "Создатель: " + botUserService.getUser(entry.getValue().getAuthorId()).getName());
								SendPhoto sendPhoto = new SendPhoto().setPhoto(entry.getValue().getPhoto()).setChatId(currentChatId);
								SendDocument sendDocumentRequest = new SendDocument();
								sendDocumentRequest.setChatId(update.getMessage().getChatId());
								sendDocumentRequest.setDocument(new File(entry.getValue().getDocxAdress()));
								sendDocumentRequest.setCaption(update.getMessage().getCaption());
								try {
									execute(sendPhoto);
									execute(sendDocumentRequest);
									ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

									replyKeyboardMarkup.setOneTimeKeyboard(true);
									KeyboardRow keyboardRow = new KeyboardRow();

									keyboardRow.add("Принять участие в проекте{"+entry.getValue().getProjectId()+"}");
									keyboardRow.add("В главное меню");
									List<KeyboardRow> keyboard = new ArrayList<>();
									keyboard.add(keyboardRow);
									replyKeyboardMarkup.setKeyboard(keyboard);
									sendMsg(currentChatId, "Выберите действие",replyKeyboardMarkup);

									currentContext.add("Принять участие в проекте");


								} catch (TelegramApiException e) {
									e.printStackTrace();
								}
							}
							break;
						}
						case "Добавить проект": {
							if (currentUser.isRegCompleated()) {
								sendMsg(currentChatId, "Добавление проекта");
								sendMsg(currentChatId, "Введите название для вашего проекта");
								currentContext.add("Добавить проект");
							}
							break;
						}
						default: {
							sendMsg(currentChatId, "Неизвестная команда.");
						}

					}
				} else {
					switch (currentContext.get(contextPosition)) {
						case "Принять участие в проекте":{

							if (currentContext.size() <= (contextPosition + 1)) {

								int projId = extractId(messageFromTelegram);


								sendMsg(currentChatId, "Введите сумму, которой вы готов поддержать проект:");
								currentContext.add("/sum{"+projId+"}");
							} else {
								switch (extractContextString(currentContext.get(++contextPosition))) {
									case "/sum": {
										Project project = projectService.getProjectById(extractId(currentContext.get(contextPosition)));
										project.setCurrentSum(project.getCurrentSum() + Integer.parseInt(messageFromTelegram));
										sendMsg(currentChatId,"Благодарим за помощь в реализации проекта");

										BotUser projectAdmin = botUserService.getUser(project.getAuthorId());

										try {
											sendSms(projectAdmin.getPhoneNumber(),currentUser.getName() + " принял участие в проекте : \n" +
													project.getTitle() + "\n" +
													"Текущая сумма  : \n" + project.getCurrentSum() + "\n"
											+ "Необходимая сумма : \n" + project.getAllSum());
										} catch (IOException e) {
											e.printStackTrace();
										}

										currentContext.clear();

										ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
										replyKeyboardMarkup.setOneTimeKeyboard(true);
										KeyboardRow keyboardRow = new KeyboardRow();
										keyboardRow.add("Регистрация");
										keyboardRow.add("Текущие проекты");
										List<KeyboardRow> keyboard = new ArrayList<>();
										keyboard.add(keyboardRow);
										if (currentUser.isRegCompleated()) {
											KeyboardRow keyboardRow2 = new KeyboardRow();
											keyboardRow2.add("Добавить проект");
											keyboard.add(keyboardRow2);
										}
										replyKeyboardMarkup.setKeyboard(keyboard);
										sendMsg(currentChatId, "Выберите действие",replyKeyboardMarkup);
										break;
									}
								}
							}
							break;
						}
						case "Регистрация": {
							if (currentContext.size() <= (contextPosition + 1)) {
								BotUser user = botUserService.getUser(currentUserId);
								user.setAddress(messageFromTelegram);
								sendMsg(currentChatId, "Введите имя:");
								currentContext.add("/name");
							} else {
								switch (currentContext.get(++contextPosition)) {
									case "/name": {
										if (currentContext.size() <= (contextPosition + 1)) {
											BotUser user = botUserService.getUser(currentUserId);
											user.setName(messageFromTelegram);
											sendMsg(currentChatId, "Введите номер:");
											currentContext.add("/number");
										} else {
											switch (currentContext.get(++contextPosition)) {
												case "/number": {
													BotUser user = botUserService.getUser(currentUserId);
													user.setPhoneNumber(messageFromTelegram);
													user.setRegCompleated(true);
													currentContext.clear();
													sendMsg(currentChatId, "Регистрация окончена.");
													ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

													replyKeyboardMarkup.setOneTimeKeyboard(true);
													KeyboardRow keyboardRow = new KeyboardRow();

													keyboardRow.add("Регистрация");
													keyboardRow.add("Текущие проекты");
													List<KeyboardRow> keyboard = new ArrayList<>();
													keyboard.add(keyboardRow);

													if (currentUser.isRegCompleated()) {
														KeyboardRow keyboardRow2 = new KeyboardRow();

														keyboardRow2.add("Добавить проект");
														keyboard.add(keyboardRow2);
													}

													replyKeyboardMarkup.setKeyboard(keyboard);
													sendMsg(currentChatId, "Выберите действие",replyKeyboardMarkup);
													break;
												}
												default: {
													sendMsg(currentChatId, "Неизвестная команда.");
												}
											}
										}
									}
								}
							}
							break;
						}
						case "Добавить проект": {

							if (currentContext.size() == (contextPosition + 1)) {
								Project project = new Project();
								project.setProjectId(Storage.PROJECT_ID++);
								project.setTitle(messageFromTelegram);
								project.setAuthorId(currentUserId);
								project.setCreateDate(LocalDateTime.now());
								projectService.addProject(project);
								sendMsg(currentChatId, "Введите описание проета:");
								sendMsg(currentChatId, "(Сообщение должно быть " +
										" не больше 100 символов)");
								currentContext.add("/description" + "{" + project.getProjectId() + "}");
							} else {
								switch (extractContextString(currentContext.get(++contextPosition))) {

									case "/description": {
										if (currentContext.size() == (contextPosition + 1)) {

											// название не должно быть больше  100 знаков
											if (messageFromTelegram.length() > 100 || messageFromTelegram.isEmpty()) {
												sendMsg(currentChatId, "Описание слишком длинное, введите еще раз:");
											} else {

												Project project = projectService.getProjectById(extractId(currentContext.get(contextPosition)));
												project.setDescription(messageFromTelegram);
												sendMsg(currentChatId, "Введите необходимую к сбору сумму, в рублях:");
												currentContext.add("/sum" + "{" + project.getProjectId() + "}");
											}
										} else {


											switch (extractContextString(currentContext.get(++contextPosition))) {
												case "/sum": {
													if (currentContext.size() == (contextPosition + 1)) {
														Project project = projectService.getProjectById(extractId(currentContext.get(contextPosition)));

														project.setAllSum(Integer.parseInt(messageFromTelegram));
														sendMsg(currentChatId, "Приложите фото: ");
														currentContext.add("/photo" + "{" + project.getProjectId() + "}");
													} else {
														switch (extractContextString(currentContext.get(++contextPosition))) {
															case "/photo": {
																Project project = projectService.getProjectById(extractId(currentContext.get(contextPosition)));
																project.setPhoto(message.getPhoto().get(0).getFileId());
																sendMsg(currentChatId, "Добавление проекта завершено.");
																currentContext.clear();
																try {
																	project.setDocxAdress(handleSimpleDoc(project));
																} catch (Exception e) {
																	e.printStackTrace();
																}
																ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

																replyKeyboardMarkup.setOneTimeKeyboard(true);
																KeyboardRow keyboardRow = new KeyboardRow();

																keyboardRow.add("Регистрация");
																keyboardRow.add("Текущие проекты");
																List<KeyboardRow> keyboard = new ArrayList<>();
																keyboard.add(keyboardRow);

																if (currentUser.isRegCompleated()) {
																	KeyboardRow keyboardRow2 = new KeyboardRow();

																	keyboardRow2.add("Добавить проект");
																	keyboard.add(keyboardRow2);
																}

																replyKeyboardMarkup.setKeyboard(keyboard);
																sendMsg(currentChatId, "Выберите действие",replyKeyboardMarkup);
																break;
															}
														}
													}
												}

											}
										}
									}
									break;
								}
							}
							break;
						}
						default: {
							sendMsg(currentChatId, "Неизвестная команда.");
						}
					}
				}
			}
		}
	}




	public String handleSimpleDoc(Project project) throws Exception {
		XWPFDocument document = new XWPFDocument();

		XWPFParagraph title = document.createParagraph();
		title.setAlignment(ParagraphAlignment.CENTER);

		//The content of a paragraph needs to be wrapped in an XWPFRun object.
// We may configure this object to set a text value and its associated styles:
		XWPFRun titleRun = title.createRun();
		titleRun.setText(project.getTitle());
		titleRun.setBold(true);
		titleRun.setFontFamily("Courier");
		titleRun.setFontSize(20);


//In a similar way we create an XWPFParagraph instance enclosing the subtitle:
		XWPFParagraph subTitle = document.createParagraph();
		subTitle.setAlignment(ParagraphAlignment.CENTER);

		XWPFRun subTitleRun = subTitle.createRun();
		subTitleRun.setText(project.getDescription());
		subTitleRun.setFontFamily("Courier");
		subTitleRun.setFontSize(16);
		/////////////////////////////
//The setTextPosition method sets the distance between the subtitle
// and the subsequent image,
		/////////////////////////////
		subTitleRun.setTextPosition(20);
//while setUnderline determines the underlining pattern.
		subTitleRun.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
		//////////////////////////////////
		///////////////////////////////////
		//We want the image to be horizontally centered and placed under the subtitle,
		// thus the following snippet must be put below the code given above:
		XWPFParagraph image = document.createParagraph();
		image.setAlignment(ParagraphAlignment.CENTER);
		////////////////////////////////////
		//Here is how to set the distance between this image and the text below it:
		///////////////////////////////////
		XWPFRun imageRun = image.createRun();
		imageRun.setTextPosition(20);
		///////////////////////////////////


		String fileAddr = "pr"+project.getProjectId()+".docx";
		try{
			FileOutputStream out = new FileOutputStream(fileAddr);
			document.write(out);
			out.close();
		}
		catch (FileNotFoundException e){
			e.fillInStackTrace();
		}
		finally {
			document.close();
		}

		return fileAddr;
	}

	private void removeLastContextElement(Integer userId) {
		BotUser currentUser = botUserService.getUser(userId);
		currentUser.getContext().remove(currentUser.getContext().size());
	}

	private void sendMsg(Long chatId, String txtMessage) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(txtMessage);

		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}


	private void sendMsg(Long chatId, String txtMessage,ReplyKeyboardMarkup markup) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(txtMessage);
		if (markup != null){
			sendMessage.setReplyMarkup(markup);
		}

		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public static int extractId(String s) {
		return Integer.parseInt(s.substring(s.lastIndexOf("{") + 1, s.lastIndexOf("}")));
	}

	public static String extractContextString(String s) {
		return s.substring(0, s.lastIndexOf("{"));
	}


	public String getBotUsername() {
		return BOT_NAME;
	}

	public static void sendSms(String number,String text) throws IOException {
		OkHttpClient client = new OkHttpClient();

		String url = "http://api.prostor-sms.ru/messages/v2/send/?phone=+"+number+"&text="+text+"&login=t89118468923&password=244764";
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = client.newCall(request).execute();

		System.out.println(response);
	}
}



