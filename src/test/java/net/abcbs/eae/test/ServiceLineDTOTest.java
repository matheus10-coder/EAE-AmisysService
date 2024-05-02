package net.abcbs.eae.test;

import static org.junit.Assert.*;

import org.junit.Test;

import net.abcbs.rpa.dto.ClaimLineDTO;

public class ServiceLineDTOTest {

	/**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ClaimLineDTO}
     *   <li>{@link ClaimLineDTO#setAllow(String)}
     *   <li>{@link ClaimLineDTO#setClaimNumber(String)}
     *   <li>{@link ClaimLineDTO#setEffectiveDate(String)}
     *   <li>{@link ClaimLineDTO#setError(String)}
     *   <li>{@link ClaimLineDTO#setPay(String)}
     *   <li>{@link ClaimLineDTO#setRow(int)}
     *   <li>{@link ClaimLineDTO#setSt(String)}
     *   <li>{@link ClaimLineDTO#getAllow()}
     *   <li>{@link ClaimLineDTO#getClaimNumber()}
     *   <li>{@link ClaimLineDTO#getEffectiveDate()}
     *   <li>{@link ClaimLineDTO#getError()}
     *   <li>{@link ClaimLineDTO#getPay()}
     *   <li>{@link ClaimLineDTO#getRow()}
     *   <li>{@link ClaimLineDTO#getSt()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ClaimLineDTO actualServiceLineDTO = new ClaimLineDTO();
        actualServiceLineDTO.setAllow("Allow");
        actualServiceLineDTO.setClaimNumber("42");
        actualServiceLineDTO.setEffectiveDate("2020-03-01");
        actualServiceLineDTO.setError("An error occurred");
        actualServiceLineDTO.setPay("Pay");
        actualServiceLineDTO.setRow(1);
        actualServiceLineDTO.setSt("St");
        assertEquals("Allow", actualServiceLineDTO.getAllow());
        assertEquals("42", actualServiceLineDTO.getClaimNumber());
        assertEquals("2020-03-01", actualServiceLineDTO.getEffectiveDate());
        assertEquals("An error occurred", actualServiceLineDTO.getError());
        assertEquals("Pay", actualServiceLineDTO.getPay());
        assertEquals(1, actualServiceLineDTO.getRow());
        assertEquals("St", actualServiceLineDTO.getSt());
    }

}
