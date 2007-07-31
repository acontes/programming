package unitTests.commandEscape;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.extra.gcmdeployment.Helpers;


public class TestCommandEscape {
    @Test
    public void testEscape() {
        String cmd = "ssh ls 'foo bar'";

        String escapedCommand = Helpers.escapeCommand(cmd);

        System.out.println(escapedCommand);

        // since we're also escaping backslashes in strings, note that the actual
        // expected string is
        // 'ssh ls '\''foo bar'\'''
        Assert.assertEquals("'ssh ls '\\''foo bar'\\'''", escapedCommand);
    }
}
