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
        
        // Ждем загрузку формы авторизации и проверяем её элементы
        $("#username").shouldBe(visible.because("Поле для ввода логина не отображается"));
        $("#password").shouldBe(visible.because("Поле для ввода пароля не отображается"));
        $("#submit-button").shouldBe(visible.because("Кнопка входа не отображается"));
        
        // Авторизация
        $("#username").setValue(USERNAME);
        $("#password").setValue(PASSWORD);
        $("#submit-button").click();
        
        // Проверка успешной авторизации
        $("#gwt-debug-editProfile").shouldBe(visible.because("Не удалось войти в систему"));
    }
    
    @AfterEach
    public void tearDown() {
        // Выход из системы
        $("#gwt-debug-logout").shouldBe(visible.because("Кнопка выхода не найдена"));
        $("#gwt-debug-logout").click();
        Selenide.closeWindow();
    }
    
    private void openSidebarIfClosed() {
        SelenideElement navContent = $("#gwt-debug-navContent");
        if (navContent.getCssValue("display").equals("none")) {
            $(".e044").click();
        }
        // Ждем загрузки панели навигации
        $("#gwt-debug-navPanel").shouldBe(visible.because("Не загрузилась панель навигации"), Duration.ofSeconds(30));
    }
    
    @Test
    public void addFavorite() {
        String title = getId();
        
        // Проверяем наличие и нажимаем кнопку добавления в избранное
        $("#gwt-debug-favorite").shouldBe(visible.because("Не найдена кнопка добавления в избранные"));
        $("#gwt-debug-favorite").click();
        
        // Проверяем появление формы добавления в избранное
        $("#gwt-debug-itemTitle-value").shouldBe(visible.because("Не появилось поле для ввода названия"));
        $("#gwt-debug-itemTitle-value").setValue(title);
        
        // Проверяем кнопку сохранения
        $("#gwt-debug-apply").shouldBe(visible.because("Не найдена кнопка сохранения"));
        $("#gwt-debug-apply").click();
        
        // Открываем сайдбар
        openSidebarIfClosed();
           // Проверяем наличие и нажимаем кнопку редактирования избранного
        $("#gwt-debug-editFavorites").shouldBe(visible.because("Не найдена кнопка редактирования избранного"));
        $("#gwt-debug-editFavorites").click();
        
        // Проверяем наличие и нажимаем кнопку удаления
        $(".del:nth-child(1)").shouldBe(visible.because("Не найдена кнопка удаления карточки"));
        $(".del:nth-child(1)").click();
        
        // Проверяем появление и работу диалога подтверждения
        $("#gwt-debug-yes").shouldBe(visible.because("Не появилось окно подтверждения удаления"));
        $("#gwt-debug-yes").click();
        
        // Сохраняем изменения
        $("#gwt-debug-apply").click();
    }
    
    @Test
    public void deleteFavorite() {
        String title = getId();
        
        // Добавляем карточку для удаления
        $("#gwt-debug-favorite").shouldBe(visible.because("Не найдена кнопка добавления в избранные"));
        $("#gwt-debug-favorite").click();
        $("#gwt-debug-itemTitle-value").setValue(title);
        $("#gwt-debug-apply").click();
        
        // Открываем сайдбар
        openSidebarIfClosed();
        
        // Проверяем наличие добавленной карточки перед удалением
        $(byXpath(String.format("//a[@id='gwt-debug-title']/div[text()='%s']", title)))
            .shouldBe(visible.because("Карточка не найдена в избранном перед удалением"));
            
        // Проверяем наличие и нажимаем кнопку редактирования избранного
        $("#gwt-debug-editFavorites").shouldBe(visible.because("Не найдена кнопка редактирования избранного"));
        $("#gwt-debug-editFavorites").click();
        
        // Проверяем наличие и нажимаем кнопку удаления
        $(".del:nth-child(1)").shouldBe(visible.because("Не найдена кнопка удаления карточки"));
        $(".del:nth-child(1)").click();
        
        // Проверяем появление и работу диалога подтверждения
        $("#gwt-debug-yes").shouldBe(visible.because("Не появилось окно подтверждения удаления"));
        $("#gwt-debug-yes").click();
        
        // Сохраняем изменения
        $("#gwt-debug-apply").click();
        
        // Проверяем что карточка удалена
        $(byXpath(String.format("//a[@id='gwt-debug-title']/div[text()='%s']", title)))
            .shouldNotBe(visible.because("Карточка не была удалена из избранного"));
    }
}