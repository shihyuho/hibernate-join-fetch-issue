# Hibernate Join, Fetch Issue

## 相關 Cases

- [HHH-17629](https://hibernate.atlassian.net/browse/HHH-17629): Criteria and Entity graph generates same join clause twice
- [HHH-18378](https://hibernate.atlassian.net/browse/HHH-18378): Avoid reusing existing joins for entity-graph fetches if they're included in the where clause
- [HHH-19095](https://hibernate.atlassian.net/browse/HHH-19095): Re-use existing compatible joins for fetch if not used in where clause (此為優化建議, 尚未實作)

## 測試案例

1. [同時呼叫 join 及 fetch, 但 where 條件至多一個](./src/test/java/com/example/JoinAndFetchWithSingleClauseTest.java)
2. [同時呼叫 join 及 fetch, 但指定兩個 where 條件](./src/test/java/com/example/JoinAndFetchWithMultiClauseTest.java)

### Summary

1. 對相同的欄位同時下 fetch 跟 join, 不會 reuse, 會產生多次 join 語句
2. 產生的 join 語句, 會依照呼叫 `root.fetch(...)` 跟 `root.join(...)` 方法的順序來產生
3. fetch 跟 join 所指定的 where 條件, 不論條件有沒有一樣, 都不會 reuse, 且會依照呼叫方法的順序來產生 where 語句
4. 若呼叫 `root.join(...)` 先於 `root.fetch(...)`, 產生的 where 語句會 "reuse" 使用 join 的來過濾

> [!NOTE]
> 第4點, 不確定是否是新的 bug, 或者是 hibernate 的優化項目, 但這跟 `HHH-19095` 有點關聯
