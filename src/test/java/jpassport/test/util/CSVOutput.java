package jpassport.test.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVOutput implements AutoCloseable
{
    private final List<String> m_curLine = new ArrayList<>();
    private final CSVPrinter m_printer;

    public CSVOutput(Path path) throws IOException
    {
        if (!Files.exists(path.getParent()))
            Files.createDirectories(path.getParent());

        m_printer = CSVFormat.DEFAULT.print(path, Charset.defaultCharset());
    }

    public void close() throws IOException
    {
        m_printer.close();
    }

    public CSVOutput add(String ... value)
    {
        m_curLine.addAll(Arrays.asList(value));
        return this;
    }

    public CSVOutput addF(double ...value)
    {
        Arrays.stream(value).forEach(dd -> m_curLine.add(Double.toString(dd)));
        return this;
    }

    public CSVOutput endLine() throws IOException
    {
        m_printer.printRecord(m_curLine);
        m_curLine.clear();
        return this;
    }
}
