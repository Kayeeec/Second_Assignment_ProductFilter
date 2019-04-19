package cz.muni.fi.pv260.productfilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ControllerTest {
	// The controller sends exactly the products selected by the provided filter to Output
	@SuppressWarnings("unchecked")
	@Test
	public void testOutputsOnlyFilteredProducts() {
		Input input = mock(Input.class);
		Output output = mock(Output.class);
		Logger log = mock(Logger.class);
		
		Controller controller = new Controller(input, output, log);
		
		Filter<Product> filter = (Filter<Product>) mock(Filter.class);
		Collection<Product> products = new LinkedList<Product>();
		Product p1 = new Product(1, "A", Color.RED, new BigDecimal(1.0));
		Product p2 = new Product(2, "B", Color.GREEN, new BigDecimal(1.0));
		Product p3 = new Product(3, "C", Color.BLUE, new BigDecimal(1.0));
		Product p4 = new Product(4, "D", Color.YELLOW, new BigDecimal(1.0));
		products.add(p1);
		products.add(p2);
		products.add(p3);
		products.add(p4);
		
		try {
			when(input.obtainProducts()).thenReturn(products);
		} catch (ObtainFailedException e) {	}

		when(filter.passes(p1)).thenReturn(true);
		when(filter.passes(p2)).thenReturn(false);
		when(filter.passes(p3)).thenReturn(true);
		when(filter.passes(p4)).thenReturn(false);
		
		controller.select(filter);
		@SuppressWarnings("rawtypes")
		Class<Collection<Product>> listClass = (Class<Collection<Product>>)(Class)Collection.class;
		ArgumentCaptor<Collection<Product>> argument = ArgumentCaptor.forClass(listClass);
		verify(output).postSelectedProducts(argument.capture());
		
		Collection<Product> outCollection = (Collection<Product>) argument.getValue();
		
		assertTrue(outCollection.contains(p1));
		assertTrue(outCollection.contains(p3));
		assertFalse(outCollection.contains(p2));
		assertFalse(outCollection.contains(p4));
	}
	
	private class InputDouble implements Input {

		@Override
		public Collection<Product> obtainProducts() throws ObtainFailedException {
			Collection<Product> products = new LinkedList<Product>();
			Product p1 = new Product(1, "A", Color.RED, new BigDecimal(1.0));
			Product p2 = new Product(2, "B", Color.GREEN, new BigDecimal(1.0));
			Product p3 = new Product(3, "C", Color.BLUE, new BigDecimal(1.0));
			Product p4 = new Product(4, "D", Color.YELLOW, new BigDecimal(1.0));
			products.add(p1);
			products.add(p2);
			products.add(p3);
			products.add(p4);
			
			return products;
		}
		
	}
	
	// The controller logs the message in documented format on success
	// With manual test double
	@SuppressWarnings("unchecked")
	@Test
	public void testLogMessageOnSuccess() {
		Input input = new InputDouble();
		Output output = mock(Output.class);
		Logger log = mock(Logger.class);
		
		Controller controller = new Controller(input, output, log);
		
		Filter<Product> filter = (Filter<Product>) mock(Filter.class);
		

		when(filter.passes(any(Product.class))).thenReturn(true);
		
		controller.select(filter);
		
		verify(log).setLevel("INFO");
		verify(log).log(Controller.class.getSimpleName(),
                "Successfully selected 4 out of 4 available products.");
	}
	
	// If exception occurs when obtaining the Product data, Controller logs this exception
	@SuppressWarnings("unchecked")
	@Test
	public void testLogMessageOnFailure() throws ObtainFailedException {
		Input input = mock(Input.class);
		Output output = mock(Output.class);
		Logger log = mock(Logger.class);
		
		Controller controller = new Controller(input, output, log);
		
		Filter<Product> filter = (Filter<Product>) mock(Filter.class);
		
		ObtainFailedException exception = new ObtainFailedException();
		when(input.obtainProducts()).thenThrow(exception);
		
		when(filter.passes(any(Product.class))).thenReturn(true);
		
		controller.select(filter);
		
		verify(log).setLevel("ERROR");
		verify(log).log(Controller.class.getSimpleName(),
                "Filter procedure failed with exception: " + exception);
	}
	
	// If exception occurs when obtaining the Product data, nothing is passed to the Output
	@SuppressWarnings("unchecked")
	@Test
	public void testNoOutputOnFailure() throws ObtainFailedException {
		Input input = mock(Input.class);
		Output output = mock(Output.class);
		Logger log = mock(Logger.class);
		
		Controller controller = new Controller(input, output, log);
		
		Filter<Product> filter = (Filter<Product>) mock(Filter.class);
		
		ObtainFailedException exception = new ObtainFailedException();
		when(input.obtainProducts()).thenThrow(exception);
		
		when(filter.passes(any(Product.class))).thenReturn(true);
		
		controller.select(filter);
		
		verifyZeroInteractions(output);
	}
	
}
