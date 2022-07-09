import clients.UserClient;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.junit4.DisplayName;
import model.User;
import model.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pageObject.MainPage;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.url;
import static junit.framework.TestCase.assertTrue;
import static pageObject.LoginPage.LOGIN_PAGE_URL;
import static pageObject.MainPage.MAIN_PAGE_URL;

public class RegisterTest {

    User user;
    UserClient userClient;
    UserCredentials userCredentials;

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        userCredentials = new UserCredentials();
    }

    @After
    public void tearDown() {
        if (userClient.loginUser(UserCredentials.from(user)).extract().statusCode() == 200) {
            String accessToken = userClient.getTokenLoginUser(UserCredentials.from(user));
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Проверка успешности регистрации")//
    public void checkSuccessRegistrationTest() {
        Selenide.open(MAIN_PAGE_URL, MainPage.class)
                .clickEnterAccountButton()
                .clickRegisterButton()
                .inputName(user.getName())
                .inputEmail(user.getEmail())
                .inputPass(user.getPassword())
                .clickRegisterButton();

        webdriver().shouldHave(url(LOGIN_PAGE_URL));
    }

    @Test
    @DisplayName("Регистрация пользователя с паролем из 5 символов")
    public void checkErrorPassRegistrationTest() {
        user = User.getRandomWithPass5Symbols();
        boolean isErrorPassFieldDisplayed = Selenide.open(MAIN_PAGE_URL, MainPage.class)
                .clickEnterAccountButton()
                .clickRegisterButton()
                .inputName(user.getName())
                .inputEmail(user.getEmail())
                .inputPass(user.getPassword())
                .clickRegisterButtonWithErrorPass()
                .isErrorPassFieldDisplayed();

        assertTrue("Должна отобразится ошибка 'Некорректный пароль'", isErrorPassFieldDisplayed);
    }
}