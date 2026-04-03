<!-- 
<img src="images/logo.png?raw=True" align="right" width="20%"/> AccessRefinery: Fast 
<img src="logo2.png?raw=True" align="right" width="20%"/> 
-->

<!-- <div style="display: flex; justify-content: space-between; align-items: center;">
    <h1 style="margin: 0;">AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud</h1>
    <img src="logo.png" width="30%">
</div> -->

# AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud

by [Ning Kang](https://xjtu-netverify.github.io/people/nkang/), [Peng Zhang ](https://xjtu-netverify.github.io/people/pzhang/) and [Jianyuan Zhang](https://xjtu-netverify.github.io/people/jyzhang/) at [ANTS lab](https://xjtu-netverify.github.io/).

![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen?logo=java)
![License](https://img.shields.io/badge/license-MIT-green)
![Paper](https://img.shields.io/badge/paper-FSE2026-orange)

> Ning Kang, Peng Zhang, Jianyuan Zhang, Hao Li, Dan Wang, Zhenrong Gu, Weibo Lin, 
> Shibiao Jiang, Zhu He, Xu Du, Longfei Chen, Jun Li, and Xiaohong Guan
> "AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud", ACM FSE 2026

## About AccessRefinery

AccessRefinery automatically mines access control intents from IAM (Identity and Access Management) policies. These intents help users verify the correctness and security of their policies. Compared with [AWS Access Analyzer](https://link.springer.com/content/pdf/10.1007/978-3-030-53288-8_9.pdf) and their [commercial system](https://docs.aws.amazon.com/IAM/latest/UserGuide/access-analyzer-concepts.html), AccessRefinery reduces mining time by 10–100× and eliminates roughly 10× redundant intents.

The key idea behind AccessRefinery for accelerating intent mining is to reduce the redundancy of multi-round SMT solving by preprocessing constraints into bit-vector constraints using our Multi-Theory Constraint Preprocessor (MCP).  
For intent reduction, AccessRefinery computes a compact set that covers all mined intents by solving a min-set-cover problem.

In addition, the MCP module supports two extra features, both fully integrated into the tool:

- Checking implication relationships for IAM policies
- Performing multi-round SMT solving, equivalent to standard SMT computations

For technical details and a full evaluation, refer to our FSE 2026 paper: [*AccessRefinery: Fast Mining Concise Access Control Intents on Public Cloud* ](https://xjtu-netverify.github.io/papers/AccessRefinery/accessrefinery_final_version.pdf).

## Install

All code, datasets (except real-world dataset), and results for the paper are contained in this repository.

Note that when browsing on an anonymous website, the page may need to be refreshed after clicking a link.


- [AccessRefinery and AWS Access Analyzer CLI version](accessrefinery/README.md)
- [Access Analyzer reproduction version](accessanalyzer/README.md)
- [Experimental Figures](experiment-figures/README.md)

<!-- Note: AWS AccessAnalyzer is accessed remotely, so only correctness experiments can be performed.
Performance experiments require a consistent environment, so we have re-implemented a version of Access Analyzer. -->

1. 功能性奖 说明可复现  
2. 可用性奖 代码结构性很好，别人可以复用
3. 公开性奖 代码挂到Zendo上面

注意：
1. 附上作者邮件，解释如何运行和安装
2. MCP解耦，AccessRefinery和MCP都附上小例子，说明如何使用
 
 
REQUIREMENTS 

STATUS

LICENSE Xijiaotong 

INSATLL
