package cz.muni.fi.pv260.productfilter;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author 448296
 */

public class AtLeastNOfFilterTest {
    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    private Filter[] createFilters(int numberOfFilters){
        return this.createRedSampleColorFilters(numberOfFilters);
    }

    private Filter[] createRedSampleColorFilters(int numberOfFilters){
        return this.createSampleColorFiltersWithColor(numberOfFilters, "red");
    }

    private Filter[] createGreenSampleColorFilters(int numberOfFilters){
        return this.createSampleColorFiltersWithColor(numberOfFilters, "green");
    }

    private Filter[] createSampleColorFiltersWithColor(int numberOfFilters, String color){
        Filter[] filters = new Filter[numberOfFilters];
        for( int i = 0; i < numberOfFilters; i ++ ){
            filters[i] = new SampleColorFilter(color);
        }
        return filters;
    }

    private Filter[] createRedAndGreenSampleColorFilters( int numberOfRedFilters, int numberOfGreenFilters ){
        Filter[] redFilters         = this.createRedSampleColorFilters(numberOfRedFilters);
        Filter[] greenFilters       = this.createGreenSampleColorFilters(numberOfGreenFilters);

        List<Filter> filters        = new ArrayList<>();
        filters.addAll( asList( redFilters ) );
        filters.addAll( asList( greenFilters ) );
        return filters.toArray( new Filter[0] );
    }

    private SampleItem createGreenSampleItem(){
        return this.createSampleItemWithColor( "green" );
    }

    private SampleItem createRedSampleItem(){
        return this.createSampleItemWithColor( "red" );
    }

    private SampleItem createSampleItemWithColor( String color ){
        return new SampleItem( color );
    }

    @Test
    public void testAtLeastNOfFilter(){
        int numberOfFilters = 1;
        Filter[] filters = this.createFilters(numberOfFilters);
        new AtLeastNOfFilter(numberOfFilters, filters);
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
        Assertions.assertThatThrownBy(() -> new AtLeastNOfFilter( numberOfFilters, filters ))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testFilterPassesIfAtLeastExactlyNChildFiltersPass() {
        int numberOfRedFilters              = 5;
        int numberOfGreenFilters            = 10;
        Filter[] filters = this.createRedAndGreenSampleColorFilters(numberOfRedFilters, numberOfGreenFilters);

        SampleItem greenSampleItem                = this.createGreenSampleItem();
        for( int expectedToPass = numberOfGreenFilters; expectedToPass > 0; expectedToPass -- ){
            AtLeastNOfFilter atLeastNOfFilter   = new AtLeastNOfFilter( expectedToPass, filters );
            assertTrue( "n is " + expectedToPass + " (less or equal than " + numberOfGreenFilters + "), but didn't pass" , atLeastNOfFilter.passes(greenSampleItem) );
        }
    }

    @Test
    public void testFilterFailsIfAtMostNMinusOneChildFilterPass(){
        int numberOfRedFilters              = 5;
        int numberOfGreenFilters            = 10;
        int totalNumberOfFilters            = numberOfGreenFilters + numberOfRedFilters;

        Filter[] filters = this.createRedAndGreenSampleColorFilters(numberOfRedFilters, numberOfGreenFilters);

        SampleItem greenSampleItem                = this.createGreenSampleItem();

        for( int expectedToPass = numberOfGreenFilters + 1; expectedToPass <= totalNumberOfFilters; expectedToPass ++ ){
            AtLeastNOfFilter atLeastNOfFilter   = new AtLeastNOfFilter( expectedToPass, filters );
            assertFalse( "n is " + expectedToPass + " (more than " + numberOfGreenFilters + "), but it passed", atLeastNOfFilter.passes(greenSampleItem) );
        }
    }

    private class SampleItem{
        private final String color;

        public String getColor() {
            return this.color;
        }

        public SampleItem(String color) {
            this.color = color;
        }
    }

    private class SampleColorFilter implements Filter<SampleItem> {
        private final String color;

        public SampleColorFilter(String color) {
            this.color = color;
        }

        @Override
        public boolean passes(SampleItem item) {
            return color.equals(item.getColor());
        }
    }
}