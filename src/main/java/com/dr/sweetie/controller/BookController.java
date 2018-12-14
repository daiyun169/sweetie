package com.dr.sweetie.controller;

import com.dr.sweetie.domain.Book;
import com.dr.sweetie.service.impl.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 获取 Book 列表
     * 处理 "/book" 的 GET 请求，用来获取 Book 列表
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getBookList(ModelMap map) {
        map.addAttribute("bookList", bookService.findAll());
        return "book_list";
    }

    /**
     * 获取创建 Book 表单
     */
    @RequestMapping(value = "/book/create", method = RequestMethod.GET)
    public String createBookForm(ModelMap map) {
        map.addAttribute("book", new Book());
        map.addAttribute("action", "create");
        return "book_form";
    }

    /**
     * 创建 Book
     * 处理 "/book/create" 的 POST 请求，用来新建 Book 信息
     * 通过 @ModelAttribute 绑定表单实体参数，也通过 @RequestParam 传递参数
     */
    @RequestMapping(value = "/book/create", method = RequestMethod.POST)
    public String postBook(@ModelAttribute Book book) {
        bookService.insertByBook(book);
        return "redirect:/";
    }

    /**
     * 获取更新 Book 表单
     * 处理 "/book/update/{id}" 的 GET 请求，通过 URL 中的 id 值获取 Book 信息
     * URL 中的 id ，通过 @PathVariable 绑定参数
     */
    @RequestMapping(value = "/book/update/{id}", method = RequestMethod.GET)
    public String getUser(@PathVariable Long id, ModelMap map) {
        map.addAttribute("book", bookService.findById(id));
        map.addAttribute("action", "update");
        return "book_form";
    }

    /**
     * 更新 Book
     * 处理 "/update" 的 PUT 请求，用来更新 Book 信息
     */
    @RequestMapping(value = "/book/update", method = RequestMethod.POST)
    public String putBook(@ModelAttribute Book book) {
        bookService.update(book);
        return "redirect:/";
    }

    /**
     * 删除 Book
     * 处理 "/book/{id}" 的 GET 请求，用来删除 Book 信息
     */
    @RequestMapping(value = "/book/delete/{id}", method = RequestMethod.GET)
    public String deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/";
    }

}
