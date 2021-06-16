package com.hepaestus.testproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.domain.Book;
import com.hepaestus.testproject.repository.BookRepository;
import com.hepaestus.testproject.repository.search.BookSearchRepository;
import com.hepaestus.testproject.service.dto.BookDTO;
import com.hepaestus.testproject.service.mapper.BookMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Book}.
 */
@Service
@Transactional
public class BookService {

    private final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final BookSearchRepository bookSearchRepository;

    public BookService(BookRepository bookRepository, BookMapper bookMapper, BookSearchRepository bookSearchRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookSearchRepository = bookSearchRepository;
    }

    /**
     * Save a book.
     *
     * @param bookDTO the entity to save.
     * @return the persisted entity.
     */
    public BookDTO save(BookDTO bookDTO) {
        log.debug("Request to save Book : {}", bookDTO);
        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);
        BookDTO result = bookMapper.toDto(book);
        bookSearchRepository.save(book);
        return result;
    }

    /**
     * Partially update a book.
     *
     * @param bookDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookDTO> partialUpdate(BookDTO bookDTO) {
        log.debug("Request to partially update Book : {}", bookDTO);

        return bookRepository
            .findById(bookDTO.getId())
            .map(
                existingBook -> {
                    bookMapper.partialUpdate(existingBook, bookDTO);
                    return existingBook;
                }
            )
            .map(bookRepository::save)
            .map(
                savedBook -> {
                    bookSearchRepository.save(savedBook);

                    return savedBook;
                }
            )
            .map(bookMapper::toDto);
    }

    /**
     * Get all the books.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BookDTO> findAll() {
        log.debug("Request to get all Books");
        return bookRepository.findAll().stream().map(bookMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one book by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookDTO> findOne(Long id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findById(id).map(bookMapper::toDto);
    }

    /**
     * Delete the book by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
        bookSearchRepository.deleteById(id);
    }

    /**
     * Search for the book corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BookDTO> search(String query) {
        log.debug("Request to search Books for query {}", query);
        return StreamSupport
            .stream(bookSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(bookMapper::toDto)
            .collect(Collectors.toList());
    }
}
