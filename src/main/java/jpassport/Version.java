package jpassport;

import java.util.Arrays;
import java.util.ResourceBundle;

public class Version
{
    private static ResourceBundle m_res;

    static
    {
        try
        {
            m_res = ResourceBundle.getBundle("jpassport.version");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static String getVersion()
    {
        return m_res.getString("version");
    }

    public static int[] getVersionParts()
    {
        String v = getVersion();

        return Arrays.stream(v.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }
}
