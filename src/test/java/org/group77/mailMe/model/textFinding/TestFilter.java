package org.group77.mailMe.model.textFinding;

import org.group77.mailMe.model.Email;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for the Filter class' filter routine.
 *
 * @author Hampus Jernkrook
 */
public class TestFilter {
    private static String[] to1;
    private static String[] to2;
    private static String[] to3;
    private static List<Email> emails;
    private final Filter<Email,String> stringFilter = new Filter<>();
    private final Filter<Email,LocalDateTime> dateFilter = new Filter<>();
    private final InFromPredicate inFromPredicate = new InFromPredicate();
    private final InToPredicate inToPredicate = new InToPredicate();
    private final OlderThanPredicate olderThanPredicate = new OlderThanPredicate();

    @BeforeAll
    public static void Setup() {
        to1 = new String[]{"TEST_nr_1@gmail.com",
                "lol@gmail.com"
        };
        to2 = new String[]{"TEST_nr_2@outlook.com",
                "lol@hotmail.com"
        };
        to3 = new String[]{"TEST_nr_3@live.com",
                "haha@gmail.com"
        };
        emails = Arrays.asList(
                new Email("mailme@gmail.com", to1, "First", "contains SAUSAGE", null,
                        LocalDateTime.of(2021, Month.OCTOBER, 11, 15, 30, 45)),
                new Email("memail@live.com", to2, "Second", "contains sausage", null,
                        LocalDateTime.of(2021, Month.MAY, 11, 15, 30, 45)),
                new Email("lol@hotmail.com", to3, "third", "contains no meat at all", null,
                        LocalDateTime.of(2010, Month.JANUARY, 11, 15, 30, 45))
        );
    }

    // ===================================================
    // TO - filter
    // ===================================================

    @Test
    public void testFindsNothingInToIfNotThere() {
        // search for a word not in any of the to:s
        List<Email> res = stringFilter.filter(emails, inToPredicate, "HELLO");
        // assert that no email is in the result.
        Assertions.assertEquals(0, res.size());
    }

    @Test
    public void testFindsTo() {
        // find the first and last element of the input.
        List<Email> res = stringFilter.filter(emails, inToPredicate, "gmail");
        Email[] expected = new Email[]{
                emails.get(0),
                emails.get(2)
        };
        //convert result to array
        Email[] actual = res.toArray(new Email[0]);
        //check that first and 4th elements are in both.
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testFindsUppercasedTo() {
        // find the first and second elements of the input
        List<Email> res = stringFilter.filter(emails, inToPredicate, "LOL");
        Email[] expected = new Email[]{
                emails.get(0),
                emails.get(1)
        };
        Email[] actual = res.toArray(new Email[0]);
        Assertions.assertArrayEquals(expected, actual);
    }

    // ===================================================
    // FROM - filter
    // ===================================================
    @Test
    public void testFindsNothingIfFromNotThere() {
        // search for a word not in the from
        List<Email> res = stringFilter.filter(emails, inFromPredicate, "stop");
        // assert that no email is in the result.
        Assertions.assertEquals(0, res.size());
    }

    @Test
    public void testFindsFrom() {
        // find the first and second emails of the input
        List<Email> res = stringFilter.filter(emails, inFromPredicate, "me");
        Email[] expected = new Email[]{
                emails.get(0),
                emails.get(1)
        };
        //convert result to array
        Email[] actual = res.toArray(new Email[0]);
        //check that first and second elements are in both.
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testFindsUppercasedFrom() {
        // find the last element of the input
        List<Email> res = stringFilter.filter(emails, inFromPredicate, "LOL");
        Email[] expected = new Email[]{
                emails.get(2)
        };
        //convert result to array
        Email[] actual = res.toArray(new Email[0]);
        //check that first and 4th elements are in both.
        Assertions.assertArrayEquals(expected, actual);
    }

    //===========================
    //  TextFinder
    //===========================

    // Running the same test on TextFinder, which calls the previously used Filter-class.
    @Test
    public void TestFilterOnFromTextFinder() {
        // find the last element of the input
        TextFinder textFinder = new TextFinder();
        List<Email> res = textFinder.filterOnFrom(emails, "LOL");
        Email[] expected = new Email[]{
                emails.get(2)
        };
        //convert result to array
        Email[] actual = res.toArray(new Email[0]);
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void TestFilterOnToTextFinder() {
        // find the first and second email
        TextFinder textFinder = new TextFinder();
        List<Email> res = textFinder.filterOnTo(emails, "LOL");
        Email[] expected = new Email[]{
                emails.get(0),
                emails.get(1)
        };
        //convert result to array
        Email[] actual = res.toArray(new Email[0]);
        Assertions.assertArrayEquals(expected, actual);
    }


    // ===================================================
    // MAX DATE  - OlderThanPredicate
    // ===================================================
    @Test
    public void TestCorrectlyClaimsOlder() {
        // see that the predicate correctly outputs that the last email is older than 2012
        Assertions.assertTrue(olderThanPredicate.test(
                emails.get(2), LocalDateTime.of(2012, Month.JANUARY, 1, 0, 0)
        ));
    }

    @Test
    public void TestCorrectlyClaimsNewer() {
        // see that the predicate correctly outputs that the last email is newer than 10th of Jan 2010
        Assertions.assertFalse(olderThanPredicate.test(
                emails.get(2), LocalDateTime.of(2010, Month.JANUARY, 10, 0, 0)
        ));
    }

    // ===================================================
    // MIN DATE  - OlderThanPredicate negated
    // ===================================================

    @Test
    public void TestCorrectlyClaimsNewer2() {
        // see that the predicate correctly outputs that the first email is newer than 2012
        Assertions.assertTrue(olderThanPredicate.negate().test(
                emails.get(0), LocalDateTime.of(2012, Month.JANUARY, 1, 0, 0)
        ));
    }

    @Test
    public void TestCorrectlyClaimsOlder2() {
        // see that the predicate correctly outputs that the last email is not newer than 31st of Jan 2010
        Assertions.assertFalse(olderThanPredicate.negate().test(
                emails.get(2), LocalDateTime.of(2010, Month.JANUARY, 31, 0, 0)
        ));
    }

    // ===================================================
    // MAX DATE  - filter
    // ===================================================

    @Test
    public void TestAllClaimedOlder() {
        // all email are older than 1st of november 2021
        List<Email> res = dateFilter.filter(emails, olderThanPredicate, LocalDateTime.of(2021, Month.NOVEMBER, 1, 0, 0));
        Email[] expected = emails.toArray(new Email[0]);
        // check that all emails are still there
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }

    @Test
    public void TestOneClaimedOlder() {
        // only one email older than 1st of Jan 2021
        List<Email> res = dateFilter.filter(emails, olderThanPredicate, LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0));
        Email[] expected = new Email[]{emails.get(2)};
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }

    // ===================================================
    // MIN DATE  - filter
    // ===================================================
    @Test
    public void TestAllClaimedNewer() {
        // all email are newer than 1st of november 2009
        List<Email> res = dateFilter.filter(emails, olderThanPredicate.negate(), LocalDateTime.of(2009, Month.NOVEMBER, 1, 0, 0));
        Email[] expected = emails.toArray(new Email[0]);
        // check that all emails are still there
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }

    @Test
    public void TestOneClaimedNewer() {
        // only one email newer than 12 of May 2021
        List<Email> res = dateFilter.filter(emails, olderThanPredicate.negate(), LocalDateTime.of(2021, Month.MAY, 12, 0, 0));
        Email[] expected = new Email[]{emails.get(0)};
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }
    // ===================================================
    // MAX DATE  - TextFinder
    // same tests as for filter but called on textFinder
    // ===================================================
    @Test
    public void TestAllClaimedOlder2() {
        // all email are older than 1st of november 2021
        List<Email> res = new TextFinder().filterOnMaxDate(emails, LocalDateTime.of(2021, Month.NOVEMBER, 1, 0, 0));
        Email[] expected = emails.toArray(new Email[0]);
        // check that all emails are still there
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }

    @Test
    public void TestOneClaimedOlder2() {
        // only one email older than 1st of Jan 2021
        List<Email> res = new TextFinder().filterOnMaxDate(emails, LocalDateTime.of(2021, Month.JANUARY, 1, 0, 0));
        Email[] expected = new Email[]{emails.get(2)};
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }

    // ===================================================
    // MIN DATE  - TextFinder
    // same tests as for filter but called on textFinder
    // ===================================================
    @Test
    public void TestAllClaimedNewer2() {
        // all email are newer than 1st of november 2009
        List<Email> res = new TextFinder().filterOnMinDate(emails, LocalDateTime.of(2009, Month.NOVEMBER, 1, 0, 0));
        Email[] expected = emails.toArray(new Email[0]);
        // check that all emails are still there
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }

    @Test
    public void TestOneClaimedNewer2() {
        // only one email newer than 12 of May 2021
        List<Email> res = new TextFinder().filterOnMinDate(emails, LocalDateTime.of(2021, Month.MAY, 12, 0, 0));
        Email[] expected = new Email[]{emails.get(0)};
        Assertions.assertArrayEquals(expected, res.toArray(new Email[0]));
    }



}