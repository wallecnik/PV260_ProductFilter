package cz.muni.fi.pv260.productfilter;

import org.junit.Test;
import org.mockito.Matchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author 422718
 */
public class ControllerTest {

    private Input in;
    private Output out;
    private Logger log;
    private Controller controller;

    @Test
    public void correctFiltering() throws ObtainFailedException {
        this.initWithMocks();

        final List<Product> products = new ArrayList<>();
        final Product p0 = new Product(0, "0", Color.BLACK, new BigDecimal(0));
        products.add(p0);
        final Product p1 = new Product(1, "1", Color.BLACK, new BigDecimal(1));
        products.add(p1);
        final Product p2 = new Product(2, "2", Color.WHITE, new BigDecimal(2));
        products.add(p2);
        final Product p3 = new Product(3, "3", Color.YELLOW, new BigDecimal(3));
        products.add(p3);
        final Product p4 = new Product(4, "4", Color.RED, new BigDecimal(4));
        products.add(p4);

        when(in.obtainProducts()).thenReturn(products);

        this.controller.select((Product product) -> product.getColor().equals(Color.BLACK));

        verify(this.out).postSelectedProducts(asList(p0, p1));
    }

    @Test
    public void successLog() throws ObtainFailedException {
        this.initWithMyLogger();

        final List<Product> products = new ArrayList<>();
        final Product p0 = new Product(0, "0", Color.BLACK, new BigDecimal(0));
        products.add(p0);
        final Product p1 = new Product(1, "1", Color.BLACK, new BigDecimal(1));
        products.add(p1);
        final Product p2 = new Product(2, "2", Color.WHITE, new BigDecimal(2));
        products.add(p2);
        final Product p3 = new Product(3, "3", Color.YELLOW, new BigDecimal(3));
        products.add(p3);
        final Product p4 = new Product(4, "4", Color.RED, new BigDecimal(4));
        products.add(p4);

        when(in.obtainProducts()).thenReturn(products);

        this.controller.select((Product product) -> product.getColor().equals(Color.BLACK));

        this.checkCorrectLog(2, 5);
    }

    @Test
    public void exceptionLogged() throws ObtainFailedException {
        this.initWithMocks();

        when(in.obtainProducts()).thenThrow(new ObtainFailedException());

        this.controller.select((Product product) -> product.getColor().equals(Color.BLACK));

        verify(this.log).log(any(), Matchers.contains("Filter procedure failed with exception: cz.muni.fi.pv260.productfilter.ObtainFailedException"));
    }

    @Test
    public void noOutputWhenException() throws ObtainFailedException {
        this.initWithMocks();

        when(in.obtainProducts()).thenThrow(new ObtainFailedException());

        this.controller.select((Product product) -> product.getColor().equals(Color.BLACK));

        verify(this.out, never()).postSelectedProducts(any());
    }

    private void initWithMocks() {
        this.in  = mock(Input.class);
        this.out = mock(Output.class);
        this.log = mock(Logger.class);

        this.controller = new Controller(this.in, this.out, this.log);
    }

    private void initWithMyLogger() {
        this.in  = mock(Input.class);
        this.out = mock(Output.class);
        this.log = new MyMockLogger();

        this.controller = new Controller(this.in, this.out, this.log);
    }

    private void checkCorrectLog(int expected, int total) {
        MyMockLogger logger = (MyMockLogger) this.log;
        assertThat(logger.getInvocations())
                .contains("Successfully selected " + expected + " out of " + total + " available products.");
    }

    private class MyMockLogger implements Logger {

        private List<String> invocations = new ArrayList<>();

        @Override
        public void setLevel(String level) {
        }

        @Override
        public void log(String tag, String message) {
            this.invocations.add(message);
        }

        public List<String> getInvocations() {
            return Collections.unmodifiableList(this.invocations);
        }
    }

}