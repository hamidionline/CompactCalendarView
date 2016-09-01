package sundeepk.github.com.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener;
import static com.github.sundeepk.compactcalendarview.CompactCalendarView.NO_FILL_LARGE_INDICATOR;
import static com.github.sundeepk.compactcalendarview.CompactCalendarView.SMALL_INDICATOR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private CompactCalendarViewListener listener;
    private CompactCalendarView compactCalendarView;
    private MainActivity activity;
    private View mainContent;

    public ApplicationTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
        listener = mock(CompactCalendarViewListener.class);
        compactCalendarView = (CompactCalendarView) activity.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setListener(listener);
        mainContent = (View) activity.findViewById(R.id.parent);
    }

    @Test
    public void testItDoesNotScrollWhenScrollingIsDisabled(){
        compactCalendarView.shouldScrollMonth(false);

        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(scroll(100, 100, -100, 0));

        verifyNoMoreInteractions(listener);

        compactCalendarView.shouldScrollMonth(true);
    }

    @Test
    public void testOnMonthScrollListenerIsCalled(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(scroll(100, 100, -100, 0));

        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        verify(listener).onMonthScroll(new Date(1425168000000L));
        verifyNoMoreInteractions(listener);
        capture("testOnMonthScrollListenerIsCalled");
    }

    @Test
    public void testItDrawNoFillLargeIndicatorOnCurrentDayWithSmallIndicatorForEvents(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        addEvents(Calendar.FEBRUARY, 2015);
        setIndicatorType(NO_FILL_LARGE_INDICATOR, SMALL_INDICATOR);
        capture("testItDrawNoFillLargeIndicatorOnCurrentDayWithSmallIndicatorForEvents");
    }

    @Test
    public void testItDrawNoFillLargeIndicatorOnCurrentDayWithNoFillLargeIndicatorForEvents(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        addEvents(Calendar.FEBRUARY, 2015);
        setIndicatorType(NO_FILL_LARGE_INDICATOR, NO_FILL_LARGE_INDICATOR);
        capture("testItDrawNoFillLargeIndicatorOnCurrentDayWithNoFillLargeIndicatorForEvents");
    }

    @Test
    public void testItDrawFillLargeIndicatorOnCurrentDayWithFillLargeIndicatorForEvents() throws InterruptedException {
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        addEvents(Calendar.FEBRUARY, 2015);
//        setIndicatorType(FILL_LARGE_INDICATOR, SMALL_INDICATOR);
        capture("testItDrawFillLargeIndicatorOnCurrentDayWithFillLargeIndicatorForEvents");
    }

    @Test
    public void testOnDayClickListenerIsCalled(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(clickXY(60, 100));

        //Tue, 03 Feb 2015 00:00:00 GMT - expected
        verify(listener).onDayClick(new Date(1422921600000L));
        verifyNoMoreInteractions(listener);
        capture("testOnDayClickListenerIsCalled");
    }

    private void setIndicatorType(final int currentDayStyle, final int eventStyle) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.setCurrentDayIndicatorStyle(currentDayStyle);
                compactCalendarView.setEventIndicatorStyle(eventStyle);
            }
        });
    }

    private void capture(final String name) {

        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.requestLayout();
                ViewHelpers.setupView(mainContent)
                        .setExactHeightPx(mainContent.getHeight())
                        .setExactWidthPx(mainContent.getWidth())
                        .layout();

                Screenshot.snap(mainContent)
                        .setName(name)
                        .record();
            }
        });
    }

    private void setDate(final Date date) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.setCurrentDate(date);
            }
        });
    }

    public ViewAction clickXY(final float x, final float y){
        final DisplayMetrics dm = activity.getResources().getDisplayMetrics() ;
        final float spX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, x, dm);
        final float spY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, y, dm);
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + spX;
                        final float screenY = screenPos[1] + spY;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    public ViewAction scroll(final int startX, final int startY, final int endX, final int endY){
        final DisplayMetrics dm = activity.getResources().getDisplayMetrics() ;
        final float spStartX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, startX, dm);
        final float spStartY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, startY, dm);
        final float spEndX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, endX, dm);
        final float spEndY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, endY, dm);
        return new GeneralSwipeAction(
                Swipe.FAST,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + spStartX;
                        final float screenY = screenPos[1] + spStartY;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + spEndX;
                        final float screenY = screenPos[1] + spEndY;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    private void addEvents(final int month, final int year) {
        Context context = compactCalendarView.getContext();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Calendar currentCalender = Calendar.getInstance();
                currentCalender.setTime(new Date());
                currentCalender.set(Calendar.DAY_OF_MONTH, 1);
                Date firstDayOfMonth = currentCalender.getTime();
                for (int i = 0; i < 6; i++) {
                    currentCalender.setTime(firstDayOfMonth);
                    if (month > -1) {
                        currentCalender.set(Calendar.MONTH, month);
                    }
                    if (year > -1) {
                        currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                        currentCalender.set(Calendar.YEAR, year);
                    }
                    currentCalender.add(Calendar.DATE, i);
                    setToMidnight(currentCalender);
                    long timeInMillis = currentCalender.getTimeInMillis();

                    List<Event> events = getEvents(timeInMillis, i);

                    compactCalendarView.addEvents(events);
                }
            }
        });
    }

    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if ( day > 2 && day <= 4) {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis) ),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
        }
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

}