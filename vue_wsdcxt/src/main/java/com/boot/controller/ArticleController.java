package com.boot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.boot.entity.Article;
import com.boot.service.ArticleService;
import com.boot.util.VeDate;
import com.github.pagehelper.Page;

@RestController //定义为控制器 返回JSON类型数据
@RequestMapping(value = "/article", produces = "application/json; charset=utf-8")// 设置请求路径
@CrossOrigin // 允许跨域访问其资源
public class ArticleController extends BaseController {
	// TODO Auto-generated method stub

	@Autowired // @Autowired的作用是自动注入依赖的ServiceBean
	private ArticleService articleService;

	// 预处理 获取基础参数
	@GetMapping(value = "createArticle.action")
	public Map<String, Object> createArticle() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("today", VeDate.getStringDateShort());
		return map;
	}

	// 新增新闻公告
	@PostMapping(value = "insertArticle.action")
	public Map<String, Object> insertArticle(@RequestBody String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Article article = new Article();
		article.setTitle(obj.getString("title")); //  为标题赋值
		article.setImage(obj.getString("image")); //  为图片赋值
		article.setContents(obj.getString("contents")); //  为内容赋值
		article.setAddtime(VeDate.getStringDateShort()); // 为发布日期赋值 
		article.setHits("0"); //  为点击数赋值
		int num = this.articleService.insertArticle(article);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "保存成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "保存失败");
		}
		return map;
	}

	// 按主键删除一个新闻公告
	@GetMapping(value = "deleteArticle.action")
	public Map<String, Object> deleteArticle(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.articleService.deleteArticle(id);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "删除成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "删除失败");
		}
		return map;
	}

	// 按主键批量删除新闻公告
	@PostMapping(value = "deleteArticleByIds.action")
	public Map<String, Object> deleteArticleByIds(@RequestBody String[] ids) {
		int num = 0;
		for (String articleid : ids) {
			num += this.articleService.deleteArticle(articleid);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "删除成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "删除失败");
		}
		return map;
	}

	// 修改新闻公告
	@PostMapping(value = "updateArticle.action")
	public Map<String, Object> updateArticle(@RequestBody String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr); // 将JSON字符串转换成object
		Article article = this.articleService.getArticleById(obj.getString("articleid")); // 获取object中articleid字段
		article.setTitle(obj.getString("title")); //  为标题赋值
		article.setImage(obj.getString("image")); //  为图片赋值
		article.setContents(obj.getString("contents")); //  为内容赋值

		Map<String, Object> map = new HashMap<String, Object>();
		int num = this.articleService.updateArticle(article);
		if (num > 0) {
			map.put("success", true);
			map.put("code", num);
			map.put("message", "修改成功");
		} else {
			map.put("success", false);
			map.put("code", num);
			map.put("message", "修改失败");
		}
		return map;
	}

	// 查询全部新闻公告数据 在下拉菜单中显示
	@GetMapping(value = "getAllArticle.action")
	public List<Article> getAllArticle() {
		return this.articleService.getAllArticle();
	}

	// 按关键字查询新闻公告数据 在下拉菜单中显示
	@GetMapping(value = "getArticleMap.action")
	public Map<String, Object> getArticleMap(String keywords) {
		Map<String, Object> map = new HashMap<String, Object>();
		Article article = new Article();
		article.setTitle(keywords);
		List<Article> list = this.articleService.getArticleByLike(article);
		map.put("data", list);
		return map;
	}

	// 通过AJAX在表格中显示新闻公告数据
	@GetMapping(value = "getArticleByPage.action")
	public Map<String, Object> getArticleByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Article> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		List<Article> list = this.articleService.getAllArticle();
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", list.size());
		map.put("data", list);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	// 通过AJAX在表格中显示新闻公告数据
	@GetMapping(value = "getArticle.action")
	public Map<String, Object> getArticle(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, String keywords) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Article> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Article article = new Article();
		article.setTitle(keywords);
		List<Article> list = this.articleService.getArticleByLike(article);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", list.size());
		map.put("data", list);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	// 通过AJAX在表格中显示新闻公告数据
	@GetMapping(value = "getOwnerArticle.action")
	public Map<String, Object> getOwnerArticle(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, String id) {
		// 定义一个Map对象 用来返回数据
		Map<String, Object> map = new HashMap<String, Object>();
		Page<Article> pager = com.github.pagehelper.PageHelper.startPage(page, limit);// 定义当前页和分页条数
		Article article = new Article();
		//article.setAdminid(id);
		List<Article> list = this.articleService.getArticleByLike(article);
		// 返回的map中定义数据格式
		map.put("count", pager.getTotal());
		map.put("total", list.size());
		map.put("data", list);
		map.put("code", 0);
		map.put("msg", "");
		map.put("page", page);
		map.put("limit", limit);
		return map;
	}

	// 按主键查询新闻公告数据
	@GetMapping(value = "getArticleById.action")
	public Article getArticleById(String id) {
		Article article = this.articleService.getArticleById(id);
		return article;
	}

	// TODO Auto-generated method stub
}


