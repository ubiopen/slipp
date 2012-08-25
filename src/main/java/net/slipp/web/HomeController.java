package net.slipp.web;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.slipp.domain.qna.QnaService;
import net.slipp.domain.qna.Question_;
import net.slipp.domain.wiki.WikiPage;
import net.slipp.domain.wiki.WikiService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	private static final int DEFAULT_PAGE_NO = 0;
	private static final int DEFAULT_PAGE_SIZE = 10;	
	
	@Resource(name="wikiService")
	private WikiService wikiService;
	
	@Resource(name = "qnaService")
	private QnaService qnaService;	
	
	@RequestMapping("/")
	public String home(Model model) {
		// model.addAttribute("pages", wikiService.findWikiPages());
		model.addAttribute("questions", qnaService.findsQuestion(createPageable()));
		model.addAttribute("tags", qnaService.findsTag());		
		return "index";
	}
	
	private Pageable createPageable() {
		Sort sort = new Sort(Direction.DESC, Question_.createdDate.getName());
		return new PageRequest(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE, sort);
	}
	
	@RequestMapping("/rss")
	public String rss(Model model) {
		List<WikiPage> pages = wikiService.findWikiPages();
		model.addAttribute("pages", pages);
		model.addAttribute("now", new Date());
		return "rss";
	}
	
	@RequestMapping("/code")
	public String code() {
		return "code";
	}
	
	@RequestMapping("/about")
	public String about() {
		return "about";
	}

    @RequestMapping("/login")
    public String login(Model model) {
        return "login";
    }
}
