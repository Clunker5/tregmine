package info.tregmine.zonemapper;

import java.io.IOException;

public interface IMapper extends AutoCloseable
{
    public void map(Zone zone) throws IOException;
}
