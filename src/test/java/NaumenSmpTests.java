import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

public class NaumenSmpTests {
    
    private static final String BASE_URL = "http://5.181.254.246:8080/sd";
    private static final String USERNAME = "User36";
    private static final String PASSWORD = "Z3Kfw*iq*2@gL2";
    
    public static String getId() {
        return UUID.randomUUID().toString();
    }
    
    @BeforeEach
    public void setUp() {
        Configuration.browser = "chrome";
        Configuration.timeout = 13000;
        Configuration.browserSize = "1140x1005";
        Selenide.open(BASE_URL);
        
        // Ждем загрузку формы авторизации
        $("#username").shouldBe(visible);
        
        // Авторизация
        $("#username").click();
        $("#username").setValue(USERNAME);
        $("#password").setValue(PASSWORD);
        $("#submit-button").click();
        
        // Проверка успешной авторизации
        $("#gwt-debug-editProfile").shouldBe(visible.because("Не удалось войти в систему"));
    }
    
    @AfterEach
    public void tearDown() {
        // Выход из системы
        $("#gwt-debug-logout").click();
        Selenide.closeWindow();
    }
    
    @Test
    public void addFavorite() {
        String title = getId();
        
        // Добавляем в избранное
        $("#gwt-debug-favorite").shouldBe(visible.because("Не найдена кнопка добавления в избранные"));
        $("#gwt-debug-favorite").click();
        
        // Ждем появления формы
        $("#gwt-debug-itemTitle-value").shouldBe(visible);
        $("#gwt-debug-itemTitle-value").setValue(title);
        $("#gwt-debug-apply").click();
        
        // Открываем сайдбар если закрыт
        SelenideElement navContent = $("#gwt-debug-navContent");
        if (navContent.getCssValue("display").equals("none")) {
            $(".e044").click();
        }
        
        // Ждем загрузки панели навигации
        $("#gwt-debug-navPanel").shouldBe(visible.because("Не загрузилась панель навигации"), Duration.ofSeconds(30));
        
        // Проверяем наличие добавленной карточки
        $(byXpath(String.format("//a[@id='gwt-debug-title']/div[text()='%s']", title)))
            .shouldBe(visible.because("Карточка не появилась в избранном"));
        
        // Удаляем тестовую карточку
        $("#gwt-debug-editFavorites").click();
        $(".del:nth-child(1)").click();
        $("#gwt-debug-yes").click();
        $("#gwt-debug-apply").click();
    }
    
    @Test
    public void deleteFavorite() {
        String title = getId();
        
        // Добавляем карточку для последующего удаления
        $("#gwt-debug-favorite").shouldBe(visible.because("Не найдена кнопка добавления в избранные"));
        $("#gwt-debug-favorite").click();
        $("#gwt-debug-itemTitle-value").setValue(title);
        $("#gwt-debug-apply").click();
        
        // Открываем сайдбар если закрыт
        SelenideElement navContent = $("#gwt-debug-navContent");
        if (navContent.getCssValue("display").equals("none")) {
            $(".e044").click();
        }
        
        // Ждем загрузки панели навигации
        $("#gwt-debug-navPanel").shouldBe(visible.because("Не загрузилась панель навигации"), Duration.ofSeconds(30));
        
        // Проверяем наличие карточки перед удалением
        $(byXpath(String.format("//a[@id='gwt-debug-title']/div[text()='%s']", title)))
            .shouldBe(visible.because("Карточка не найдена в избранном"));
        
        // Удаляем карточку
        $("#gwt-debug-editFavorites").click();
        $(".del:nth-child(1)").click();
        $("#gwt-debug-yes").click();
        $("#gwt-debug-apply").click();
        
        // Проверяем что карточка удалена
        $(byXpath(String.format("//a[@id='gwt-debug-title']/div[text()='%s']", title)))
            .shouldNotBe(visible.because("Карточка не была удалена из избранного"));
    }
}