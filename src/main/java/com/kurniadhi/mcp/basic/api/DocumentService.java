package com.kurniadhi.mcp.basic.api;

import com.kurniadhi.mcp.basic.api.dto.DocumentRequest;
import com.kurniadhi.mcp.basic.api.dto.DocumentResponse;
import com.kurniadhi.mcp.basic.api.dto.SearchRequest;
import com.kurniadhi.mcp.basic.api.dto.SearchResult;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    public DocumentService(JdbcTemplate jdbcTemplate, EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    public void insert(DocumentRequest request) {
        byte[] embed = embeddingService.embedToBytes(request.value());
        jdbcTemplate.update(
                "INSERT INTO documents (key, value, embed) VALUES (?, ?, ?)",
                request.key(), request.value(), embed
        );
    }
    // is a container object introduced in Java 8 that may or may not contain
    //a single non-null value, it helps prevent null point exception and eliminate boilerplate checks
    public Optional<DocumentResponse> findByKey(String key)
    {
        //when called try select from the collumn key and value from the document table where thee key
        //is equal to data gotten from DocumentResponse (it does this by using the result sets methods)
        try
        {
            DocumentResponse result = jdbcTemplate.queryForObject(
                    "SELECT key, value FROM documents WHERE key = ?",
                    (rs, rowNum) -> new DocumentResponse(rs.getString("key"), rs.getString("value")),
                    key
            );
            return Optional.ofNullable(result);
        }
        //return Optional.empty to avoid null point exception
        catch (EmptyResultDataAccessException e)
        {
            return Optional.empty();
        }
    }
    //method that returns a List of strings that is filled with keys
    // from the document table that are ordered
    public List<String> findAllKeys()
    {
        return jdbcTemplate.queryForList("SELECT key FROM documents ORDER BY key", String.class);
    }
    public void updateValue(String key, String newVal)
    {
        byte[] embed = embeddingService.embedToBytes(newVal);
        jdbcTemplate.update("UPDATE documents SET value = ?, embed = ? WHERE key = ?", newVal, embed, key);
    }

    public void deleteByKey(String key)
    {
        jdbcTemplate.update("DELETE FROM documents WHERE key = ?", key);
    }

    public List<SearchResult> search(SearchRequest request) {
        byte[] queryEmbed = embeddingService.embedToBytes(request.query());
        return jdbcTemplate.query(
                "SELECT key, value, vector_distance_cosine(embed, ?) AS distance FROM documents ORDER BY distance LIMIT ?",
                (rs, rowNum) -> new SearchResult(
                        rs.getString("key"),
                        rs.getString("value"),
                        rs.getDouble("distance")
                ),
                queryEmbed, request.limit()
        );
    }
}
