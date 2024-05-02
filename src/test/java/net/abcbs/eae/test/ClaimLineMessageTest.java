package net.abcbs.eae.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.abcbs.eae.jaxrs.ClaimLineMessage;

public class ClaimLineMessageTest {
	static final String CLAIMLINEMESSAGE = "Not all who wander are lost";
	
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link ClaimLineMessage#ClaimLineMessage()}
     *   <li>{@link ClaimLineMessage#setId(int)}
     *   <li>{@link ClaimLineMessage#setMessage(String)}
     *   <li>{@link ClaimLineMessage#getId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ClaimLineMessage actualClaimLineMessage = new ClaimLineMessage();
        actualClaimLineMessage.setId(1);
        actualClaimLineMessage.setMessage(CLAIMLINEMESSAGE);
        assertEquals(1, actualClaimLineMessage.getId());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link ClaimLineMessage#ClaimLineMessage(String)}
     *   <li>{@link ClaimLineMessage#setId(int)}
     *   <li>{@link ClaimLineMessage#setMessage(String)}
     *   <li>{@link ClaimLineMessage#getId()}
     * </ul>
     */
    @Test
    public void testConstructor2() {
        ClaimLineMessage actualClaimLineMessage = new ClaimLineMessage(CLAIMLINEMESSAGE);
        actualClaimLineMessage.setId(1);
        actualClaimLineMessage.setMessage(CLAIMLINEMESSAGE);
        assertEquals(1, actualClaimLineMessage.getId());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link ClaimLineMessage#ClaimLineMessage(String, int)}
     *   <li>{@link ClaimLineMessage#setId(int)}
     *   <li>{@link ClaimLineMessage#setMessage(String)}
     *   <li>{@link ClaimLineMessage#getId()}
     * </ul>
     */
    @Test
    public void testConstructor3() {
        ClaimLineMessage actualClaimLineMessage = new ClaimLineMessage(CLAIMLINEMESSAGE, 1);
        actualClaimLineMessage.setId(1);
        actualClaimLineMessage.setMessage(CLAIMLINEMESSAGE);
        assertEquals(1, actualClaimLineMessage.getId());
    }

    /**
     * Method under test: {@link ClaimLineMessage#printMessage()}
     */
    @Test
    public void testPrintMessage() {
        assertEquals("Not all who wander are lost", (new ClaimLineMessage(CLAIMLINEMESSAGE)).printMessage());
    }

    /**
     * Method under test: {@link ClaimLineMessage#getMessage(String)}
     */
    @Test
    public void testGetMessage() {
        assertEquals("Not all who wander are lost",
                (new ClaimLineMessage(CLAIMLINEMESSAGE)).getMessage(CLAIMLINEMESSAGE));
    }
}
