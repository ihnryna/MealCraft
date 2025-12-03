package org.l5g7.mealcraft.unittest.aspects;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.aop.PageLoadTimeAspect;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageLoadTimeAspectTest {

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private PageLoadTimeAspect pageLoadTimeAspect;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        // Set up logger capturing
        logger = (Logger) LoggerFactory.getLogger("performanceLogger");
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void measurePageLoadTime_SuccessfulExecution_LogsPerformance() throws Throwable {
        // Arrange
        String methodName = "HomeController.index()";
        String expectedResult = "home-page";

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(proceedingJoinPoint.proceed()).thenAnswer(invocation -> {
            await().atMost(Duration.ofMillis(1000))
                    .until(() -> true); // Simulate some processing time
            return expectedResult;
        });

        // Act
        Object result = pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);

        // Assert
        assertEquals(expectedResult, result);
        assertEquals(1, listAppender.list.size());

        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains("Page handled by"));
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
        assertTrue(logEvent.getFormattedMessage().contains("ms"));
        assertTrue(logEvent.getFormattedMessage().contains("ns"));

        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void measurePageLoadTime_FastExecution_LogsSmallDuration() throws Throwable {
        // Arrange
        String methodName = "ApiController.status()";
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(proceedingJoinPoint.proceed()).thenReturn("OK");

        // Act
        Object result = pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);

        // Assert
        assertEquals("OK", result);
        assertEquals(1, listAppender.list.size());

        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains(methodName));

        // Extract and verify duration is logged (even if very small)
        String message = logEvent.getFormattedMessage();
        assertTrue(message.matches(".*\\d+\\.\\d+ ms.*"));
        assertTrue(message.matches(".*\\d+ ns.*"));
    }
    @Test
    void measurePageLoadTime_ExceptionThrown_StillLogsTiming() throws Throwable {
        // Arrange
        String methodName = "ErrorController.error()";
        RuntimeException exception = new RuntimeException("Controller error");

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(proceedingJoinPoint.proceed()).thenThrow(exception);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);
        });

        // Verify timing was still logged despite exception
        assertEquals(1, listAppender.list.size());

        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals("INFO", logEvent.getLevel().toString());
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
        assertTrue(logEvent.getFormattedMessage().contains("ms"));

        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void measurePageLoadTime_ReturnsNullResult_LogsSuccessfully() throws Throwable {
        // Arrange
        String methodName = "VoidController.action()";
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        // Act
        Object result = pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);

        // Assert
        assertNull(result);
        assertEquals(1, listAppender.list.size());

        ILoggingEvent logEvent = listAppender.list.get(0);
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
    }

    @Test
    void measurePageLoadTime_MultipleInvocations_LogsSeparately() throws Throwable {
        // Arrange
        String methodName1 = "Controller1.method1()";
        String methodName2 = "Controller2.method2()";

        when(signature.toShortString())
                .thenReturn(methodName1)
                .thenReturn(methodName2);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.proceed())
                .thenReturn("result1")
                .thenReturn("result2");

        // Act
        pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);
        pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);

        // Assert
        assertEquals(2, listAppender.list.size());

        ILoggingEvent logEvent1 = listAppender.list.get(0);
        ILoggingEvent logEvent2 = listAppender.list.get(1);

        assertTrue(logEvent1.getFormattedMessage().contains(methodName1));
        assertTrue(logEvent2.getFormattedMessage().contains(methodName2));

        verify(proceedingJoinPoint, times(2)).proceed();
    }

    @Test
    void measurePageLoadTime_DurationFormatting_ContainsBothMsAndNs() throws Throwable {
        // Arrange
        String methodName = "TestController.test()";
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(proceedingJoinPoint.proceed()).thenReturn("test");

        // Act
        pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);

        // Assert
        assertEquals(1, listAppender.list.size());

        ILoggingEvent logEvent = listAppender.list.get(0);
        String message = logEvent.getFormattedMessage();

        // Verify both milliseconds and nanoseconds are in the message
        assertTrue(message.matches(".*\\d+\\.\\d+ ms.*"));
        assertTrue(message.matches(".*\\d+ ns.*"));
    }

    @Test
    void measurePageLoadTime_CheckedExceptionThrown_StillLogsTiming() throws Throwable {
        // Arrange
        String methodName = "DataController.fetchData()";
        Exception exception = new Exception("Data fetch failed");

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(proceedingJoinPoint.proceed()).thenThrow(exception);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);
        });

        // Verify timing was logged
        assertEquals(1, listAppender.list.size());

        ILoggingEvent logEvent = listAppender.list.get(0);
        assertTrue(logEvent.getFormattedMessage().contains(methodName));
    }

    @Test
    void measurePageLoadTime_PreservesProceedReturn_ReturnsOriginalValue() throws Throwable {
        // Arrange
        String methodName = "UserController.getUser()";
        TestResult expectedResult = new TestResult("success", 42);

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(methodName);
        when(proceedingJoinPoint.proceed()).thenReturn(expectedResult);

        // Act
        Object result = pageLoadTimeAspect.measurePageLoadTime(proceedingJoinPoint);

        // Assert
        assertSame(expectedResult, result);
        TestResult actualResult = (TestResult) result;
        assertEquals("success", actualResult.status);
        assertEquals(42, actualResult.value);

        verify(proceedingJoinPoint, times(1)).proceed();
    }

    // Helper class for testing
    private static class TestResult {
        String status;
        int value;

        TestResult(String status, int value) {
            this.status = status;
            this.value = value;
        }
    }
}