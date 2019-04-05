package run.halo.app.web.controller.portal.api;

import run.halo.app.model.dto.TagOutputDTO;
import run.halo.app.model.dto.post.PostSimpleOutputDTO;
import run.halo.app.model.entity.Tag;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import run.halo.app.model.dto.TagOutputDTO;
import run.halo.app.model.dto.post.PostSimpleOutputDTO;
import run.halo.app.model.entity.Tag;
import run.halo.app.service.PostTagService;
import run.halo.app.service.TagService;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Portal tag controller.
 *
 * @author johnniang
 * @date 4/2/19
 */
@RestController("PortalTagController")
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    private final PostTagService postTagService;

    public TagController(TagService tagService, PostTagService postTagService) {
        this.tagService = tagService;
        this.postTagService = postTagService;
    }

    @GetMapping
    @ApiOperation("Lists tags")
    public List<? extends TagOutputDTO> listTags(@SortDefault(sort = "updateTime", direction = DESC) Sort sort,
                                                 @ApiParam("If the param is true, post count of tag will be returned")
                                                 @RequestParam(name = "more", required = false, defaultValue = "false") Boolean more) {
        if (more) {
            return postTagService.listTagWithCountDtos(sort);
        }
        return tagService.convertTo(tagService.listAll(sort));
    }

    @GetMapping("{slugName}/posts")
    @ApiOperation("Lists posts by tag slug name")
    public Page<PostSimpleOutputDTO> listPostsBy(@PathVariable("slugName") String slugName,
                                                 @PageableDefault(sort = "updateTime", direction = DESC) Pageable pageable) {
        // Get tag by slug name
        Tag tag = tagService.getBySlugNameOfNonNull(slugName);

        // Get posts, convert and return
        return postTagService.pagePostsBy(tag.getId(), pageable).map(post -> new PostSimpleOutputDTO().convertFrom(post));
    }
}