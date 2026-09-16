package com.kurniadhi.mcp.basic.api;

import com.kurniadhi.mcp.basic.api.dto.DocumentRequest;
import com.kurniadhi.mcp.basic.api.dto.DocumentResponse;
import com.kurniadhi.mcp.basic.api.dto.SearchRequest;
import com.kurniadhi.mcp.basic.api.dto.SearchResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

//rest controller marks it as a spring bean and a http handler
@RestController
//all endpoints in this class starts with /api
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    //constructor to set the DocumentService obj
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    @Tool(description = "Insert a new document with a key and value")
    //makes it so that insert() has postmapping so that when a POST arives at /api/documents,
    //the method runs
    @PostMapping("/documents")
    public ResponseEntity<Void> insert(@RequestBody DocumentRequest request)
    {
        documentService.insert(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Tool(description = "Find a document by its key")
    //makes it so that findByKey() has GetMapping which mean when a get request hits
    //api/documents/example, it will run findByKey("example"
    @GetMapping("/documents/{key}")
    //@PathVariable here tells spring to grab a segment (in this case, key)
    //and puts it into the method param
    public ResponseEntity<DocumentResponse> findByKey(@PathVariable String key) {
        return documentService.findByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //update the values
    @Tool(description = "Update a document's value by key")
    @PutMapping("/documents/{key}")
    public ResponseEntity<Void> update(@PathVariable String key, @RequestBody DocumentRequest request) {
        documentService.updateValue(key, request.value());
        return ResponseEntity.ok().build();
    }
    @Tool(description = "List all document keys")
    //this one says that if a get request gets sent and hits /keys, run this
    @GetMapping("/keys")
    public List<String> findAllKeys()
    {
        return documentService.findAllKeys();
    }
    @Tool(description = "Delete a document by key")
    @DeleteMapping("/documents/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key)
    {
        documentService.deleteByKey(key);
        return ResponseEntity.ok().build();
    }
    @Tool(description = "Semantic search across documents by query text")
    @PostMapping("/search")
    public List<SearchResult> search(@RequestBody SearchRequest request)
    {
        return documentService.search(request);
    }
}
