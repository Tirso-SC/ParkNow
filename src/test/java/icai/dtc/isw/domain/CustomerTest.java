package icai.dtc.isw.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("1", "Cristiano Ronaldo");
    }

    @Test
    void testConstructorVacio() {
        Customer c = new Customer();
        assertNotNull(c.getId());
        assertNotNull(c.getName());
    }

    @Test
    void testGetId() {
        assertEquals("1", customer.getId());
    }

    @Test
    void testSetId() {
        customer.setId("2");
        assertEquals("2", customer.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Cristiano Ronaldo", customer.getName());
    }

    @Test
    void testSetName() {
        customer.setName("Lionel Messi");
        assertEquals("Lionel Messi", customer.getName());
    }
}
