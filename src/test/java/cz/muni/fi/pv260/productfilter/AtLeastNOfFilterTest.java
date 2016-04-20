package cz.muni.fi.pv260.productfilter;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author
 */
public class AtLeastNOfFilterTest {
    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    public Filter[] createFilters(int numberOfFilters){
        // TODO: refactor this (return some generic filters)
        return this.createRedColorFilters(numberOfFilters);
    }

    public Filter[] createRedColorFilters(int numberOfFilters){
        Filter[] filters = new Filter[numberOfFilters];
        for( int i = 0; i < numberOfFilters; i ++ ){
            filters[i] = new ColorFilter(Color.RED);
        }
        return filters;
    }

    @Test
    public void testAtLeastNOfFilter(){
        int numberOfFilters = 1;
        Filter[] filters = this.createFilters(numberOfFilters);
        AtLeastNOfFilter atLeastNOfFilter = new AtLeastNOfFilter(numberOfFilters, filters);

        assertTrue("Unable to create an object filter", atLeastNOfFilter != null);
    }

    @Test
    public void testAtLeastNOfFilterNeverSucceeds() {
        int numberOfFilters = 0;
        Filter[] filters = this.createFilters(numberOfFilters);
        Assertions.assertThatThrownBy(() -> new AtLeastNOfFilter(numberOfFilters + 1, filters))
                .isExactlyInstanceOf(FilterNeverSucceeds.class);
    }

    @Test
    public void testAtLeastIllegalArgumentException() {
        int numberOfFilters = 0;
        Filter[] filters = this.createFilters(numberOfFilters);
        Assertions.assertThatThrownBy(() -> new AtLeastNOfFilter(numberOfFilters, filters))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

}