package com.leo.hbase.manager.web.controller.system;

import java.util.List;

import com.github.CCweixiao.util.StrUtil;
import com.leo.hbase.manager.common.utils.HBaseConfigUtils;
import com.leo.hbase.manager.framework.shiro.session.OnlineSession;
import com.leo.hbase.manager.framework.shiro.session.OnlineSessionDAO;
import com.leo.hbase.manager.system.domain.SysUserOnline;
import com.leo.hbase.manager.system.service.ISysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.leo.hbase.manager.common.config.Global;
import com.leo.hbase.manager.common.core.controller.BaseController;
import com.leo.hbase.manager.framework.util.ShiroUtils;
import com.leo.hbase.manager.system.domain.SysMenu;
import com.leo.hbase.manager.system.domain.SysUser;
import com.leo.hbase.manager.system.service.ISysConfigService;
import com.leo.hbase.manager.system.service.ISysMenuService;

/**
 * 首页 业务处理
 *
 * @author ruoyi
 */
@Controller
public class SysIndexController extends BaseController {
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private OnlineSessionDAO onlineSessionDAO;

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap) {
        // 取身份信息
        SysUser user = ShiroUtils.getSysUser();
        // 根据用户id取出菜单
        List<SysMenu> menus = menuService.selectMenusByUser(user);
        mmap.put("menus", menus);
        mmap.put("user", user);
        mmap.put("sideTheme", configService.selectConfigByKey("sys.index.sideTheme"));
        mmap.put("skinName", configService.selectConfigByKey("sys.index.skinName"));
        mmap.put("copyrightYear", Global.getCopyrightYear());
        mmap.put("demoEnabled", Global.isDemoEnabled());
        String sessionId = ShiroUtils.getSessionId();
        SysUserOnline online = userOnlineService.selectOnlineById(sessionId);
        String defaultClusterCode = HBaseConfigUtils.getAllClusterAlias().get(0);

        String currentHBaseClusterCode;
        if (online == null) {
            currentHBaseClusterCode = defaultClusterCode;
        } else {
            OnlineSession onlineSession = (OnlineSession) onlineSessionDAO.readSession(sessionId);
            if (onlineSession == null) {
                currentHBaseClusterCode = defaultClusterCode;
            } else {
                currentHBaseClusterCode = onlineSession.getCluster();
            }
        }
        if (StrUtil.isBlank(currentHBaseClusterCode)) {
            currentHBaseClusterCode = defaultClusterCode;
        }
        mmap.put("currentHBaseClusterCode", currentHBaseClusterCode);
        return "index";
    }

    // 切换主题
    @GetMapping("/system/switchSkin")
    public String switchSkin(ModelMap mmap) {
        return "skin";
    }

    //切换集群
    @GetMapping("/system/switchCluster")
    public String switchCluster(ModelMap mmap) {
        List<String> clusterCodes = HBaseConfigUtils.getAllClusterAlias();
        mmap.put("clusterCodes", clusterCodes);
        return "cluster";
    }

    // 系统介绍
    @GetMapping("/system/main")
    public String main(ModelMap mmap) {
        mmap.put("version", Global.getVersion());
        return "main";
    }
}
