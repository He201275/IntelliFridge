package ovh.intellifridge.intellifridge;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by franc on 17-11-16.
 */
public class ProfileActivityTest {
    ProfileActivity profileActivity;

    @Before
    public void setUp() throws Exception {
        profileActivity = new ProfileActivity();
    }

    @Test
    public void onCreate() throws Exception {

    }

    @Test
    public void getUserData() throws Exception {

    }

    @Test
    public void isEmptyString() throws Exception {
        String test = "";
        assertThat(profileActivity.isEmptyString(test),is(true));
        String test1 = "a";
        assertThat(profileActivity.isEmptyString(test1),is(false));
        String test2 = "null";
        assertThat(profileActivity.isEmptyString(test2),is(true));
        String test3 = null;
        assertThat(profileActivity.isEmptyString(test3),is(true));
    }

}