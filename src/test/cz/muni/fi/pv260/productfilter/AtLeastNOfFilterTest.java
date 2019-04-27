package cz.muni.fi.pv260.productfilter;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Task 4 - AtLeastNOfFilter - test the following behavior:
 * – The constructor throws the exceptions as documented
 * – The filter passes if at least exactly n child filters pass
 * – The filter fails if at most n-1 child filters pass
 *
 * + Try to only use the AtLeastNOfFilter class itself in the tests, do not depend on other project classes.
 * + Use Mockito at least once to create one of the Test Doubles needed.
 * + Create at least one Test Double manually (without the use of any mocking framework).
 */
@SuppressWarnings("unchecked")
public class AtLeastNOfFilterTest {
    private Filter<String> mockedFilter1 = mock(Filter.class);
    private Filter<String> mockedFilter2 = mock(Filter.class);
    private Filter<String> mockedFilter3 = mock(Filter.class);

    /**
     * The constructor throws the exceptions as documented
     */
    @Test(expected = FilterNeverSucceeds.class)
    public void throwsFilterNeverSucceedsIfLessThanNFilters() {
        Filter filter1 = new ManualFilterMock<String>();
        Filter filter2 = new ManualFilterMock<String>();

        new AtLeastNOfFilter<String>(3, filter1, filter2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionIfNIs_Zero() {
        new AtLeastNOfFilter<>(0, mockedFilter1, mockedFilter2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionIfNIs_Minus_1() {
        new AtLeastNOfFilter<>(-1, mockedFilter1, mockedFilter2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIllegalArgumentExceptionIfNIs_Minus_20() {
        new AtLeastNOfFilter<>(-20, mockedFilter1, mockedFilter2);
    }

    /**
     * The filter passes if at least exactly n child filters pass
     */
    @Test
    public void filterPassesIfNChildFiltersPass() {
        when(mockedFilter1.passes(anyString())).thenReturn(true);
        when(mockedFilter2.passes(anyString())).thenReturn(true);
        when(mockedFilter3.passes(anyString())).thenReturn(false);
        assertTrue(getFilter(2).passes("item"));
    }

    /**
     * The filter fails if at most n-1 child filters pass
     */
    @Test
    public void filterFailsIfLessThanNChildFiltersPass() {
        //2
        when(mockedFilter1.passes(anyString())).thenReturn(true);
        when(mockedFilter2.passes(anyString())).thenReturn(true);
        when(mockedFilter3.passes(anyString())).thenReturn(false);
        assertFalse(getFilter(3).passes("item"));
        //1
        when(mockedFilter2.passes(anyString())).thenReturn(false);
        assertFalse(getFilter(3).passes("item"));
        //0
        when(mockedFilter1.passes(anyString())).thenReturn(false);
        assertFalse(getFilter(3).passes("item"));
    }

    private AtLeastNOfFilter<String> getFilter(int n) {
        return new AtLeastNOfFilter<>(n, mockedFilter1, mockedFilter2, mockedFilter3);
    }

    private class ManualFilterMock<T> implements Filter<T> {
        ManualFilterMock() { }

        @Override
        public boolean passes(T item) {
            return true;
        }
    }
}