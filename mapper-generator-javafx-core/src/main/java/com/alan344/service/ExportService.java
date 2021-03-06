package com.alan344.service;

import org.mybatis.generator.my.config.MybatisExportConfig;
import com.alan344.constants.NodeConstants;
import com.alan344.controller.ExportSuccessAlertController;
import com.alan344.controller.MybatisSetupController;
import com.alan344.controller.component.MybaitsExportController;
import com.alan344.service.generator.MapperGeneratorStrategyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author AlanSun
 * @date 2020/9/11 8:57
 */
@Slf4j
@Service
public class ExportService {

    @Resource
    private MapperGeneratorStrategyContext mapperGeneratorStrategyContext;

    @Resource
    private TableService tableService;

    @Resource
    private ColumnService columnService;

    @Resource
    private MybatisSetupController mybatisSetUpController;

    @Resource
    private MybaitsExportController mybaitsExportController;

    @Resource
    private ExportSuccessAlertController exportSuccessAlertController;

    /**
     * 导出
     */
    public void export() {
        final MybatisExportConfig mybatisExportConfig = mybaitsExportController.getConfig();
        // 保存配置
        mybatisSetUpController.addConfig(mybatisExportConfig);

        // 导出时，如果 tableNameIsOverrideRecodeMap 不为空，则把 table 配置（如 insert）文件重写
        tableService.downLoadTableIfOverrideModify();

        // 导出时，如果 tableNameIsOverrideRecodeMap 不为空，则把 columns 文件重写
        columnService.downLoadColumnOverride();

        boolean exportSuccess = true;
        try {
            // 调用 mybatis generator 生成文件
            mapperGeneratorStrategyContext.getMapperGeneratorStrategy(mybatisExportConfig).generator(mybatisExportConfig);
        } catch (Throwable e) {
            log.error("export fail", e);
            exportSuccess = false;
        }

        // 成功或失败后的弹窗
        exportSuccessAlertController.openTableAdvancedSetup(NodeConstants.primaryStage, exportSuccess, mybatisExportConfig);
    }
}
