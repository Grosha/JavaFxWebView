/*
 * Copyright 2018 1C-Soft LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com._1c.qa.selenium.fxdriver;

import com._1c.qa.selenium.fxdriver.robot.FxRobot;
import com.google.common.collect.ImmutableMap;
import javafx.stage.Stage;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.*;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FxDriver implements WebDriver, TakesScreenshot, HasInputDevices {
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private Capabilities capabilities;
    private FxSearchContext context;
    private SessionId sessionId;
    private Robot robot;
    private FxMouse mouse;
    private FxKeyboard keyboard;

    public FxDriver(Capabilities capabilities)
    {
        FxRobot fxRobot = new FxRobot();
        this.capabilities = capabilities;
        this.context = new FxSearchContext(fxRobot);
        this.mouse = new FxMouse(fxRobot);
        this.keyboard = new FxKeyboard(fxRobot);

        try
        {
            this.robot = new Robot();
        }
        catch (AWTException e)
        {
            throw new WebDriverException(e);
        }
    }

    @Override
    public void get(String s)
    {
        throw new UnsupportedOperationException("Navigation to the specified url is not supported in JavaFX application");
    }

    @Override
    public String getCurrentUrl()
    {
        return "http://javafx-driver.invalid";
    }

    @Override
    public String getTitle()
    {
        return NodeUtils.listWindows().stream()
                .map(window -> (Stage)window)
                .map(Stage::getTitle)
                .findFirst()
                .orElse("");
    }

    @Override
    public List<WebElement> findElements(By by)
    {
        return by.findElements(context);
    }

    @Override
    public WebElement findElement(By by)
    {
        return by.findElement(context);
    }

    @Override
    public String getPageSource()
    {
        return "";
    }

    @Override
    public void close()
    {
        executor.schedule(FxServer.server::stop, 1, TimeUnit.SECONDS);
    }

    @Override
    public void quit()
    {
        executor.schedule(() -> System.exit(0), 1, TimeUnit.SECONDS);
    }

    @Override
    public Set<String> getWindowHandles()
    {
        return NodeUtils.listWindows().stream().map(Object::toString).collect(Collectors.toSet());
    }

    @Override
    public String getWindowHandle()
    {
        return String.valueOf(NodeUtils.getCurrentProcessId());
    }

    @Override
    public TargetLocator switchTo()
    {
        return null;
    }

    @Override
    public Navigation navigate()
    {
        return new FxDriverNavigation();
    }

    @Override
    public Options manage()
    {
        return new FxDriverOptions();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException
    {
        try
        {
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

            try (ByteArrayOutputStream stream = new ByteArrayOutputStream())
            {
                ImageIO.write(screenshot, "png", stream);
                stream.flush();
                return target.convertFromPngBytes(stream.toByteArray());
            }
        }
        catch (IOException e)
        {
            throw new WebDriverException(e);
        }
    }

    @Override
    public Keyboard getKeyboard()
    {
        return this.keyboard;
    }

    @Override
    public Mouse getMouse()
    {
        return this.mouse;
    }

    private class FxDriverOptions implements Options {
        @Override
        public void addCookie(Cookie cookie) {

        }

        @Override
        public void deleteCookieNamed(String s) {

        }

        @Override
        public void deleteCookie(Cookie cookie) {

        }

        @Override
        public void deleteAllCookies() {

        }

        @Override
        public Set<Cookie> getCookies() {
            return null;
        }

        @Override
        public Cookie getCookieNamed(String s) {
            return null;
        }

        @Override
        public Timeouts timeouts() {
            return null;
        }

        @Override
        public ImeHandler ime() {
            return null;
        }

        @Override
        public Window window() {
            return new FxDriverWindow();
        }

        @Override
        public Logs logs() {
            return null;
        }
    }

    private class FxDriverWindow implements Window {
        @Override
        public void setSize(Dimension dimension) {

        }

        @Override
        public void setPosition(Point point) {

        }

        @Override
        public Dimension getSize() {
            return null;
        }

        @Override
        public Point getPosition() {
            return null;
        }

        @Override
        public void maximize() {

        }

        @Override
        public void fullscreen() {

        }
    }

    private class FxDriverNavigation implements Navigation {
        @Override
        public void back() {

        }

        @Override
        public void forward() {

        }

        @Override
        public void to(String s) {

        }

        @Override
        public void to(URL url) {

        }

        @Override
        public void refresh() {

        }
    }
}
