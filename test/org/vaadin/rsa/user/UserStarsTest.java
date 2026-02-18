package org.vaadin.rsa.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserStarsTest {

    @Test
    public void testGetStars() {
        assertEquals(1, UserStars.ONE_STAR.getStars());
        assertEquals(2, UserStars.TWO_STARS.getStars());
        assertEquals(3, UserStars.THREE_STARS.getStars());
        assertEquals(4, UserStars.FOUR_STARS.getStars());
        assertEquals(5, UserStars.FIVE_STARS.getStars());
    }
}