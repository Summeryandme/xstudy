package com.xstudy.media.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstudy.media.model.po.MediaProcess;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

  @Select(
      "SELECT * FROM media_process mp where mp.id % #{shardTotal} = #{shardIndex} LIMIT #{count}")
  List<MediaProcess> listShard(
      @Param("shardTotal") int shardTotal,
      @Param("shardIndex") int shardIndex,
      @Param("count") int count);
}
