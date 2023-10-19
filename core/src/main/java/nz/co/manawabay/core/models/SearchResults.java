package nz.co.manawabay.core.models;

public interface SearchResults {

    String getHeader();

    String getQueryString();

    String getSearchRootPagePath();

    long getPageNumber();
}
